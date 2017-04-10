package viewer.util;

import java.awt.image.BufferedImage;
import java.io.File;

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

public class CreateThumbUtils extends MediaListenerAdapter {

	private int mVideoStreamIndex = -1;
	private String OutputPath = "";

	double timeBase = 0;
	int videoStreamId = -1;

	public void createThumbUtils() {

	}

	public void createThumb(File inputFile, String OutputPath) {

		this.OutputPath = OutputPath;

		IMediaReader reader = ToolFactory.makeReader(inputFile.getPath());
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
			File outputFile = new File(OutputPath);

			ImageIO.write(sumbImage, "png", outputFile);

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

	private void seekToMs(IContainer container, long timeMs) {
	    if(videoStreamId == -1) {
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