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
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;

public class Test0418 extends MediaListenerAdapter {
	private static final String INPUT_DIR = "D:\\testtest";
	private static final String OUTPUT_DIR = "D:\\testtest\\ss";

	private static final String[] SUFFIX_ARRAY = {"wmv","mp4","flv"};

	File[] files = new File(INPUT_DIR).listFiles();
	private int fileIndex = 0;
	private int thumbIndex = 1;
	private boolean proccessFlg = false;

	private static final Logger log = LoggerFactory.getLogger(Test0418.class);

	public static void main(String[] args) {
		new Test0418();
	}

	public Test0418() {

		for (File file : files) {
			if (!Arrays.asList(SUFFIX_ARRAY).contains(getSuffix(file.getName()))) {
				fileIndex++;
				continue;
			}

	        thumbIndex = 1;
	        long totalDuration = 0;
			while (thumbIndex < 6) {
				try {
					IMediaReader reader = ToolFactory.makeReader(file.getPath());
					reader.open();
					reader.setBufferedImageTypeToGenerate(5);
					reader.addListener(this);

					IContainer container = reader.getContainer();
					int videoStreamId = -1;
					double timeBase = 0;

					log.info("# Duration (ms): " + container.getDuration() / 1000);
					long thumbBetweenDurationMs = container.getDuration() / 5000;

		            IStream stream = container.getStream(0);
		            timeBase = stream.getTimeBase().getDouble();

					if (thumbIndex == 1) {
						seekToMs(container, 10000, videoStreamId, timeBase);
					} else {
						seekToMs(container, totalDuration, videoStreamId, timeBase);
					}
					totalDuration += thumbBetweenDurationMs;
					proccessFlg = false;
					while (reader.readPacket() == null && !proccessFlg) {
					}
					thumbIndex += 1;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			fileIndex++;
		}
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			BufferedImage sumbImage = reSize3(event.getImage(), 100,90);
			File outputFile = new File(OUTPUT_DIR, "_smb" + thumbIndex + "_" + files[fileIndex].getName() + ".png");
			log.info("# Create: " + "_smb" + thumbIndex + "_" + files[fileIndex].getName() + ".png");

			ImageIO.write(sumbImage, "png", outputFile);
			proccessFlg = true;

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
	    log.info("# seekTo: " + seekTo);
	    container.seekKeyFrame(0, seekTo, IContainer.SEEK_FLAG_BACKWARDS);
	}
}