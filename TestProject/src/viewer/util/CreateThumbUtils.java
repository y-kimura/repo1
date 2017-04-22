package viewer.util;

import java.awt.image.BufferedImage;
import java.io.File;

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

import viewer.ApplicationContext;
import viewer.view.FileListPanel;

public class CreateThumbUtils extends MediaListenerAdapter {

	private File outputFile;
	private boolean proccessFlg = false;
	private static final Logger log = LoggerFactory.getLogger(FileListPanel.class);

	public void createThumbUtils() {
	}

	public void createThumb(File inputFile, File outputFile, int thumbIndex) {

		this.outputFile = outputFile;

		IMediaReader reader = null;
		try {
			long totalDuration = 0;

			try {

				reader = ToolFactory.makeReader(inputFile.getPath());
				reader.open();
				reader.setBufferedImageTypeToGenerate(5);
				reader.addListener(this);

				int videoStreamId = -1;
				double timeBase = 0;
				IContainer container = reader.getContainer();

				long thumbBetweenDurationMs = container.getDuration() / (1000 * (ApplicationContext.MAX_THUMB + 1));

	            IStream stream = container.getStream(0);
	            timeBase = stream.getTimeBase().getDouble();

				totalDuration = thumbBetweenDurationMs * thumbIndex;
				seekToMs(reader.getContainer(), totalDuration, videoStreamId, timeBase);

				proccessFlg = false;
				while (reader.readPacket() == null && !proccessFlg) {
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				reader.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		}
		reader.close();
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			BufferedImage sumbImage = reSize3(event.getImage(), 100, 80);

			ImageIO.write(sumbImage, "png", outputFile);
			proccessFlg = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage reSize3(BufferedImage img, int width, int height) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(img, null);
		return rescaled;
	}

	private void seekToMs(IContainer container, long timeMs, int videoStreamId, double timeBase) {
	    long seekTo = (long) (timeMs/1000.0/timeBase);
	    container.seekKeyFrame(0, seekTo, IContainer.SEEK_FLAG_BACKWARDS);
	}
}