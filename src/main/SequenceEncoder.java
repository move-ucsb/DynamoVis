/*

  	DynamoVis Animation Tool
    Copyright (C) 2016 Glenn Xavier
    UPDATED: 2021 Mert Toka

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
*/

package main;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;

public class SequenceEncoder implements PropertyChangeListener {
    private SeekableByteChannel ch;
    private Picture toEncode;
    private RgbToYuv420 transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;
    private ProgressMonitor progressMonitor;
    DesktopPane parent;
    private int previousExportCounter;
    Task operation;
    int start;
    int end;
    boolean deleteTemp;

    public SequenceEncoder(DesktopPane father, String name, int s, int e, boolean del) throws IOException {
        parent = father;
        File file = new File("export/"+name);
        start = s;
        end = e;
        deleteTemp = del;
        previousExportCounter = parent.exportCounter;
        this.ch = NIOUtils.writableFileChannel(file);

        // Transform to convert between RGB and YUV
        transform = new RgbToYuv420(0, 0);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

        // Allocate a buffer big enough to hold output frames
        _out = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of encoder
        encoder = new H264Encoder();

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();

        progressMonitor = new ProgressMonitor(parent, "Encoding Video...", "", 0, 100);
        progressMonitor.setProgress(0);
        operation = new Task(this);
        operation.addPropertyChangeListener(this);
        operation.execute();

    }

    class Task extends SwingWorker<Void, Void> {

        SequenceEncoder encoder;

        public Task(SequenceEncoder se) {
            encoder = se;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (int i = start; i < end; i++) {
                BufferedImage bi = ImageIO.read(new File(
                        String.format("export/temp/" + parent.animationTitle + parent.exportCounter + "/temp%08d.jpeg", i)));
                encoder.encodeImage(bi);
                setProgress((int) (100 * i) / end);
            }
            encoder.finish();
            setProgress(100);
            parent.exportCounter++;
            return null;
        }

        public void done() {
            try {
                @SuppressWarnings("unused")
                Void result = get();
                System.out.println("Video Encoding Completed");
                
                if(deleteTemp) {
                    // Delete temp folder
                    Files.walk(Path.of("export/temp/"+ parent.animationTitle + previousExportCounter))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                }
            } catch (InterruptedException e) {

            } catch (CancellationException e) {
                System.out.println("Encoding Cancelled...\n");
            } catch (ExecutionException e) {
                System.out.println("Encoding Failed: " + e.getCause());
            } catch (IOException e) {
                System.out.println("Failed to clear \"temp\" folder...\n");
                e.printStackTrace();
            }
        }
    }

    public void encodeImage(BufferedImage bi) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(bi.getWidth(), bi.getHeight(), ColorSpace.YUV420);
        }

        // Perform conversion
        for (int i = 0; i < 3; i++)
            Arrays.fill(toEncode.getData()[i], 0);
        transform.transform(AWTUtil.fromBufferedImage(bi), toEncode);

        // Encode image into H.264 frame, the result is stored in '_out' buffer
        _out.clear();
        ByteBuffer result = encoder.encodeFrame(_out, toEncode);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.encodeMOVPacket(result, spsList, ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, frameNo, 25, 1, frameNo, true, null, frameNo, 0));

        frameNo++;
    }

    public void finish() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);

        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
        }
        if (progressMonitor.isCanceled()) {
            operation.cancel(true);
        }

    }
}
