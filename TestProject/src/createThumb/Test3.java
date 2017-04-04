/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
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
import com.xuggle.xuggler.IContainer;

public class Test3 extends MediaListenerAdapter {

	public static final double SECONDS_BETWEEN_FRAMES = 5.0D;
	public static final long MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND * 5.0D);

	private static final Logger log = LoggerFactory.getLogger(Test3.class);
//	private static String INPUT_DIR = "D:\\testtest";
//	private static String OUTPUT_DIR = "D:\\testtest\\ss";

	private static String INPUT_DIR = "E:\\test";
	private static String OUTPUT_DIR = "E:\\test\\ss";

	String[] suffixArray = {"wmv","mp4","flv"};

	private int mVideoStreamIndex = -1;
	File[] files = new File(INPUT_DIR).listFiles();
	private int fileIndex = 0;

	private int smbIndex = 0;

	private long betweenFrame = 0;

	private static long mLastPtsWrite = Global.NO_PTS;

	public static void main(String[] args) {
		new Test3();
	}

	public Test3() {

		for (File file : files) {
			if (!Arrays.asList(suffixArray).contains(getSuffix(file.getName()))) {
				fileIndex++;
				continue;
			}

			IContainer container = IContainer.make();
		    if (container.open(file.getPath(), IContainer.Type.READ, null) < 0) {
		        throw new RuntimeException("Cannot open '" + file.getPath() + "'");
		    }

		    log.info("# Duration (ms): " + ((container.getDuration() == Global.NO_PTS) ? "unknown" : "" + container.getDuration() / 100));
		    log.info("# File size (bytes): " + container.getFileSize());
		    log.info("# Bit rate: " + container.getBitRate());

			IMediaReader reader = ToolFactory.makeReader(file.getPath());

			reader.setBufferedImageTypeToGenerate(5);

//			reader.getContainer().seekKeyFrame(streamIndex, timestamp, flags);

			reader.addListener(this);

			betweenFrame = container.getDuration() / 5;

			smbIndex = 0;

			mLastPtsWrite = Global.NO_PTS;

			try {
				while (reader.readPacket() == null) {
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
			if (mLastPtsWrite == Global.NO_PTS) {
				mLastPtsWrite = event.getTimeStamp().longValue()
						- betweenFrame;
			}
			if (event.getTimeStamp().longValue() - mLastPtsWrite >= betweenFrame) {
				BufferedImage sumbImage = reSize3(event.getImage(), 50,50);
				File outputFile = new File(OUTPUT_DIR, "_smb_" + smbIndex + "_".concat(files[fileIndex].getName()).concat(".png"));

				ImageIO.write(sumbImage, "png", outputFile);

				log.info("mLastPtsWrite=" + mLastPtsWrite + "_betweenFrame=" + betweenFrame + "_filename=" + outputFile.getName());

				smbIndex++;
				mLastPtsWrite += betweenFrame;
			}

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
}