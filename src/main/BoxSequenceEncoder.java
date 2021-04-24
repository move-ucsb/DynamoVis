// package main;

// import java.awt.image.BufferedImage;
// import java.beans.PropertyChangeEvent;
// import java.beans.PropertyChangeListener;
// import java.io.File;
// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.concurrent.CancellationException;
// import java.util.concurrent.ExecutionException;
// import javax.imageio.ImageIO;
// import javax.swing.ProgressMonitor;
// import javax.swing.SwingWorker;

// import org.jcodec.codecs.h264.H264Encoder;
// import org.jcodec.codecs.h264.H264Utils;
// import org.jcodec.common.NIOUtils;
// import org.jcodec.common.SeekableByteChannel;
// import org.jcodec.common.model.ColorSpace;
// import org.jcodec.common.model.Picture;
// import org.jcodec.containers.mp4.Brand;
// import org.jcodec.containers.mp4.MP4Packet;
// import org.jcodec.containers.mp4.TrackType;
// import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
// import org.jcodec.containers.mp4.muxer.MP4Muxer;
// import org.jcodec.scale.AWTUtil;
// import org.jcodec.scale.RgbToYuv420;

// public class BoxSequenceEncoder implements PropertyChangeListener {
//     private SeekableByteChannel ch;
//     private Picture toEncode;
//     private RgbToYuv420 transform;
//     private H264Encoder encoder;
//     private ArrayList<ByteBuffer> spsList;
//     private ArrayList<ByteBuffer> ppsList;
//     private FramesMP4MuxerTrack outTrack;
//     private ByteBuffer _out;
//     private int frameNo;
//     private MP4Muxer muxer;
//     private ProgressMonitor progressMonitor;
//     TimeBoxPanel parent;
//     Task operation;
//     int start;
//     int end;

//     public BoxSequenceEncoder(TimeBoxPanel father, String name, int s, int e) throws IOException {
//         parent = father;
//         File file = new File(name);
//         start = s;
//         end = e;
//         this.ch = NIOUtils.writableFileChannel(file);

//         // Transform to convert between RGB and YUV
//         transform = new RgbToYuv420(0, 0);

//         // Muxer that will store the encoded frames
//         muxer = new MP4Muxer(ch, Brand.MP4);

//         // Add video track to muxer
//         outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

//         // Allocate a buffer big enough to hold output frames
//         _out = ByteBuffer.allocate(1920 * 1080 * 6);

//         // Create an instance of encoder
//         encoder = new H264Encoder();

//         // Encoder extra data ( SPS, PPS ) to be stored in a special place of
//         // MP4
//         spsList = new ArrayList<ByteBuffer>();
//         ppsList = new ArrayList<ByteBuffer>();

//         progressMonitor = new ProgressMonitor(parent, "Encoding Video...", "", 0, 100);
//         progressMonitor.setProgress(0);
//         operation = new Task(this);
//         operation.addPropertyChangeListener(this);
//         operation.execute();

//     }

//     class Task extends SwingWorker<Void, Void> {

//         BoxSequenceEncoder encoder;

//         public Task(BoxSequenceEncoder se) {
//             encoder = se;
//         }

//         @Override
//         protected Void doInBackground() throws Exception {
//             for (int i = start; i < end; i++) {
//                 BufferedImage bi = ImageIO.read(new File(String
//                         .format("temp/" + parent.animationTitle + parent.exportCounter + "_3D/temp%08d.jpeg", i)));
//                 encoder.encodeImage(bi);
//                 setProgress((int) (100 * i) / end);
//             }
//             encoder.finish();
//             setProgress(100);
//             parent.exportCounter++;
//             return null;
//         }

//         public void done() {
//             try {
//                 @SuppressWarnings("unused")
//                 Void result = get();
//                 System.out.println("Video Encoding Completed");
//             } catch (InterruptedException e) {

//             } catch (CancellationException e) {
//                 System.out.println("Encoding Cancelled...\n");
//             } catch (ExecutionException e) {
//                 System.out.println("Encoding Failed: " + e.getCause());
//             }
//         }
//     }

//     public void encodeImage(BufferedImage bi) throws IOException {
//         if (toEncode == null) {
//             toEncode = Picture.create(bi.getWidth(), bi.getHeight(), ColorSpace.YUV420);
//         }

//         // Perform conversion
//         for (int i = 0; i < 3; i++)
//             Arrays.fill(toEncode.getData()[i], 0);
//         transform.transform(AWTUtil.fromBufferedImage(bi), toEncode);

//         // Encode image into H.264 frame, the result is stored in '_out' buffer
//         _out.clear();
//         ByteBuffer result = encoder.encodeFrame(_out, toEncode);

//         // Based on the frame above form correct MP4 packet
//         spsList.clear();
//         ppsList.clear();
//         H264Utils.encodeMOVPacket(result, spsList, ppsList);

//         // Add packet to video track
//         outTrack.addFrame(new MP4Packet(result, frameNo, 25, 1, frameNo, true, null, frameNo, 0));

//         frameNo++;
//     }

//     public void finish() throws IOException {
//         // Push saved SPS/PPS to a special storage in MP4
//         outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

//         // Write MP4 header and finalize recording
//         muxer.writeHeader();
//         NIOUtils.closeQuietly(ch);
//     }

//     @Override
//     public void propertyChange(PropertyChangeEvent evt) {
//         if ("progress" == evt.getPropertyName()) {
//             int progress = (Integer) evt.getNewValue();
//             progressMonitor.setProgress(progress);
//         }
//         if (progressMonitor.isCanceled()) {
//             operation.cancel(true);
//         }

//     }
// }
