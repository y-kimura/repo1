package createThumb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class Test extends MediaListenerAdapter {
	private static final String INPUT_DIR = "D:\\testtest";
	private static final String OUTPUT_DIR = "D:\\testtest\\ss";

	private static final String[] SUFFIX_ARRAY = {"wmv","mp4","flv"};

	File[] files = new File(INPUT_DIR).listFiles();
	private int fileIndex = 0;
	private int thumbIndex = 1;

	private static final Logger log = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {
		new Test();
	}

	public Test() {

		for (File file : files) {
			if (!Arrays.asList(SUFFIX_ARRAY).contains(getSuffix(file.getName()))) {
				fileIndex++;
				continue;
			}
			IMediaReader reader = ToolFactory.makeReader(file.getPath());
			try {
				reader.open();
				reader.setBufferedImageTypeToGenerate(5);
				reader.addListener(this);

				IContainer container = reader.getContainer();
				if (container.getDuration() == Global.NO_PTS) {
					continue;
				}
				log.info("# Duration (ms): " + container.getDuration() / 100);
				long thumbBetweenDurationMs = container.getDuration() / 500;

				int videoStreamId = -1;
				double timeBase = 0;
		        for(int i = 0; i < container.getNumStreams(); i++) {
		            IStream stream = container.getStream(i);
		            IStreamCoder coder = stream.getStreamCoder();
		            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
		                videoStreamId = i;
		                timeBase = stream.getTimeBase().getDouble();
		                break;
		            }
		        }

				long totalDuration = 0;
				for (int i = 0; i < 5; i++) {
					if (i == 0) {
						seekToMs(container, 10000, videoStreamId, timeBase);
					} else {
						seekToMs(container, totalDuration, videoStreamId, timeBase);
					}
					totalDuration += thumbBetweenDurationMs;
					thumbIndex = i + 1;
					reader.readPacket();
				}

			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				reader.close();
			}
			fileIndex++;
		}
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			BufferedImage sumbImage = reSize3(event.getImage(), 90,75);
			File outputFile = new File(OUTPUT_DIR, "_smb" + thumbIndex + "_" + files[fileIndex].getName() + ".png");

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

	private void seekToMs(IContainer container, long timeMs, int videoStreamId, double timeBase) {
		log.info("# Seek (ms): " + timeMs);
	    long seekTo = (long) (timeMs/1000.0/timeBase);
	    container.seekKeyFrame(videoStreamId, seekTo, IContainer.SEEK_FLAG_BACKWARDS);
	}
}