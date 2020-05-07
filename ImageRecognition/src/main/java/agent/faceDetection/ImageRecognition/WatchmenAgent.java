package agent.faceDetection.ImageRecognition;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class WatchmenAgent extends AgentBase{

	private static final long serialVersionUID = 1L;

	public static final String NICKNAME = "Watchmen";

	static JFrame frame = new JFrame();
	static JLabel lbl = new JLabel();;
	static ImageIcon icon;

	public WatchmenAgent() {
		frame.setLayout(new FlowLayout());
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	protected void setup(){
		super.setup();
		this.type = AgentModel.WATCHMEN;
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		addBehaviour(new Watchmen());
		registerAgentDF();
	}

	private class Watchmen extends CyclicBehaviour{

		public void reset() {
			super.reset();
			System.out.println("Reset del agente");
		}

		@Override
		public void action() {
			if(cameraWatch()) {
				AgentContainer c = getContainerController();
				AgentController a;
				try {
					a = c.createNewAgent(AlertAgent.NICKNAME+Math.random()*100, AlertAgent.class.getName(), new Object[]{"0"});
					a.start();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("Nadie a la vista");
			}
		}

		/**
		 * Analiza la cámara. Si detecta una cara en un determinado tiempo, la variable producirá True
		 * @return
		 */
		public boolean cameraWatch() {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			CascadeClassifier lbpcascade = new CascadeClassifier("C:\\OpenCV\\opencv\\build\\etc\\lbpcascades\\lbpcascade_frontalface.xml");
			CascadeClassifier cascadeEyeClassifier = new CascadeClassifier("C:\\OpenCV\\opencv\\build\\etc\\haarcascades\\haarcascade_eye.xml");
			CascadeClassifier cascadeNoseClassifier = new CascadeClassifier("C:\\OpenCV\\opencv\\build\\etc\\haarcascades\\haarcascade_mcs_nose.xml");
			boolean output = false;
			long t= System.currentTimeMillis();
			long end = t+5000;

			VideoCapture videoDevice = new VideoCapture();

			videoDevice.open(0);
			if (videoDevice.isOpened()) {
				while(System.currentTimeMillis() < end) {
					Mat frameCapture = new Mat();
					videoDevice.read(frameCapture);
					MatOfRect faces = new MatOfRect();
					Mat cropped = new Mat();

					lbpcascade.detectMultiScale(frameCapture, faces);								
					for (Rect rect : faces.toArray()) {
						Imgproc.putText(frameCapture, "Face", new Point(rect.x+50,rect.y-5), 3, 1, new Scalar(255,255,255));								
						Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 45, 0),5);
						cropped = frameCapture.submat(new Rect(rect.x,rect.y,rect.width,rect.height));
					}

					MatOfRect eyes = new MatOfRect();
					cascadeEyeClassifier.detectMultiScale(cropped, eyes);
					for (Rect rect : eyes.toArray()) {
						Imgproc.putText(cropped, "Eye", new Point(rect.x,rect.y-5), 2, 1, new Scalar(255,255,255));				
						Imgproc.rectangle(cropped, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(200, 200, 100),1);
					}

					MatOfRect nose = new MatOfRect();
					cascadeNoseClassifier.detectMultiScale(cropped, nose);
					for (Rect rect : nose.toArray()) {
						Imgproc.putText(cropped, "Nose", new Point(rect.x,rect.y-5), 2, 1, new Scalar(255,255,255));				
						Imgproc.rectangle(cropped, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(50, 255, 50),1);
					}
					PushImage(MatToImage(frameCapture));

					if(eyes.toArray().length == 2 && nose.toArray().length == 1) {
						output = true;
					}
				}
			} else {
				System.out.println("Error.");
			}
			videoDevice.release();
			return output;
		}

		/**
		 * Convierte Mat a imagen
		 * https://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		 * 
		 * @param mat
		 * @return
		 */
		public BufferedImage MatToImage(Mat mat) {
			int type = 0;
			if (mat.channels() == 1) {
				type = BufferedImage.TYPE_BYTE_GRAY;
			} else if (mat.channels() == 3) {
				type = BufferedImage.TYPE_3BYTE_BGR;
			} else {
				return null;
			}
			BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
			WritableRaster raster = image.getRaster();
			DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
			byte[] data = dataBuffer.getData();
			mat.get(0, 0, data);
			return image;
		}

		/**
		 * Envía a JFrame la imagen de la cámara
		 * @param img2
		 */
		public void PushImage(Image img2) {
			icon = new ImageIcon(img2);
			lbl.setIcon(icon);
			frame.add(lbl);
			frame.revalidate();
		}
	}
}