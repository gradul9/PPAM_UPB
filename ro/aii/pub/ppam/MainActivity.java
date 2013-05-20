package ro.aii.pub.ppam;

import java.awt.font.NumericShaper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static int RESULT_LOAD_IMAGE = 1;
	private Bitmap bmp;
	private ImageView imageView2;
	private float r, g, b;
	private int alpha, i;
	private boolean ok[];
	private Bitmap dest;
	private final int numberOfThreads = 16;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView2 = (ImageView) findViewById(R.id.img_grayscale);

		ok = new boolean[numberOfThreads];
		for (i = 0; i < ok.length; i++) {
			ok[i] = false;
		}
		i = 0;
		Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			bmp = BitmapFactory.decodeFile(picturePath);
			
			TextView tv1 = (TextView) findViewById(R.id.tv_original_picture);
			tv1.setText("Original Image");
			ImageView imageView = (ImageView) findViewById(R.id.imgView);
			imageView.setImageBitmap(bmp);

			final int picw = bmp.getWidth();
			final int pich = bmp.getHeight();

			

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					try{
					dest = Bitmap.createBitmap(picw, pich, bmp.getConfig());
					Thread[] threads = new Thread[numberOfThreads];
					for (i = 0; i < numberOfThreads; i++) {
						threads[i] = new Thread(new Runnable() {

							@Override
							public void run() {
								try{
								ok[i] = convertPieceToGrayScale(picw, pich,	numberOfThreads, i);
								}catch(Exception e){
									e.printStackTrace();
								}

							}
						});
						
						threads[i].start();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					boolean okToMoveOn = false;

					while (okToMoveOn == false) {
						okToMoveOn = true;
						for (int j = 0; j < ok.length; j++) {
							if (ok[j] == false) {
								okToMoveOn = false;
								break;
							}
						}

					}

					createImmage(dest);
					
					}catch(Exception e){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								CharSequence text = "Picture to big!";
								Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
								
							}
						});
					}

				}
			});
			t.start();

		}
	}

	private void createImmage(final Bitmap dest) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				imageView2.setImageBitmap(dest);
				TextView tv2 = (TextView) findViewById(R.id.tv_grayscale);
				tv2.setText("Grayscale");
			}
		});

	}

	private boolean convertPieceToGrayScale(int width, int height,
			int modifier, int start) {

		int fromY = start * height / modifier;
		int toY = (start + 1) * height/ modifier;
		int fromX = start * width / modifier;
		int toX = (start + 1) * width/ modifier;
		
		System.out.println("from " + fromY + " to " + toY + " Y");
		System.out.println("from " + fromX + " to " + toX + " X");
		for (int y = fromY; y < toY; y++) {
			for (int x = 0; x < width; x++) {

				int pixel = bmp.getPixel(x, y);

				r = Color.red(pixel) * 0.2989f;
				g = Color.green(pixel) * 0.587f;
				b = Color.blue(pixel) * 0.114f;
				alpha = Color.alpha(pixel);
				int pixelBW = (int) (r + g + b);

				int newPixel = Color.argb(alpha, pixelBW, pixelBW, pixelBW);
				dest.setPixel(x, y, newPixel);
			}
		}
		return true;
	}
}
