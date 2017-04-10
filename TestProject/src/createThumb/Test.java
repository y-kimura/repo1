package createThumb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class Test extends MediaListenerAdapter {
	private static String INPUT_DIR = "D:\\testtest";
	private static String OUTPUT_DIR = "D:\\testtest\\ss";

	String[] suffixArray = {"wmv","mp4","flv"};

	private int mVideoStreamIndex = -1;
	File[] files = new File(INPUT_DIR).listFiles();
	private int fileIndex = 0;

	double timeBase = 0;
	int videoStreamId = -1;

	public static void main(String[] args) {
		new Test();
	}

	public Test() {

		for (File file : files) {
			if (!Arrays.asList(suffixArray).contains(getSuffix(file.getName()))) {
				fileIndex++;
				continue;
			}
			IMediaReader reader = ToolFactory.makeReader(file.getPath());
			try {
				reader.open();

				timeBase = 0;
				videoStreamId = -1;
				seekToMs(reader.getContainer(), 10000);

				reader.setBufferedImageTypeToGenerate(5);

				reader.addListener(this);

				while (reader.readPacket() == null && mVideoStreamIndex == -1) {
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
			}
			reader.close();
			mVideoStreamIndex = -1;
			fileIndex++;
		}
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			if (event.getStreamIndex().intValue() != this.mVideoStreamIndex) {
				if (-1 == this.mVideoStreamIndex) {
					this.mVideoStreamIndex = event.getStreamIndex().intValue();
				} else {
					return;
				}
			}
			BufferedImage sumbImage = reSize3(event.getImage(), 90,75);
//			File outputFile = File.createFile("frame", ".png", new File(OUTPUT_DIR));
			File outputFile = new File(OUTPUT_DIR, "_smb_".concat(files[fileIndex].getName()).concat(".png"));

			ImageIO.write(sumbImage, "png", outputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}

	private static BufferedImage reSize3(BufferedImage img, int width, int height) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(img, null);
		return rescaled;
	}

	private void seekToMs(IContainer container, long timeMs) {
	    if(videoStreamId == -1) {
	    	int test = container.getNumStreams();
	    	IStream stream2 = container.getStream(0);
	        for(int i = 0; i < container.getNumStreams(); i++) {
	            IStream stream = container.getStream(i);
	            IStreamCoder coder = stream.getStreamCoder();
	            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
	                videoStreamId = i;
	                timeBase = stream.getTimeBase().getDouble();
	                break;
	            }
	        }
	    }

	    long seekTo = (long) (timeMs/1000.0/timeBase);
	    container.seekKeyFrame(videoStreamId, seekTo, IContainer.SEEK_FLAG_BACKWARDS);
	}
}