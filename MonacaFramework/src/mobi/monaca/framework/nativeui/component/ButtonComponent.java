package mobi.monaca.framework.nativeui.component;

import static mobi.monaca.framework.nativeui.UIUtil.TAG;
import static mobi.monaca.framework.nativeui.UIUtil.createBitmapWithColorFilter;
import static mobi.monaca.framework.nativeui.UIUtil.updateJSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mobi.monaca.framework.nativeui.ComponentEventer;
import mobi.monaca.framework.nativeui.DefaultStyleJSON;
import mobi.monaca.framework.nativeui.NonScaleBitmapDrawable;
import mobi.monaca.framework.nativeui.UIContext;
import mobi.monaca.framework.nativeui.UIUtil;
import mobi.monaca.framework.nativeui.component.view.MonacaButton;
import mobi.monaca.framework.nativeui.exception.NativeUIException;
import mobi.monaca.framework.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class ButtonComponent extends ToolbarComponent {

	protected UIContext context;
	protected FrameLayout layout;
	protected MonacaButton button;
	protected MonacaImageButton imageButton;
	protected ComponentEventer eventer;
	protected static String[] validKeys = {
		"component",
		"style",
		"iosStyle",
		"androidStyle",
		"id",
		"event"
	};

	public ButtonComponent(UIContext context, JSONObject buttonJSON) throws NativeUIException{
		super(buttonJSON);
		this.context = context;
		buildEventer();
		initView();
	}

	private void buildEventer(){
		this.eventer = new ComponentEventer(context, getComponentJSON().optJSONObject("event"));
	}

	public ComponentEventer getUIEventer() {
		return eventer;
	}

	protected void initView() {
		layout = new FrameLayout(context);
		layout.setClickable(true);

		button = new MonacaButton(context);
		button.getButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				eventer.onTap();
			}
		});
		button.getInnerImageButton().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						eventer.onTap();
					}
				});

		imageButton = new MonacaImageButton(context);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				eventer.onTap();
			}
		});

		layout.addView(button);
		layout.addView(imageButton);

		style();
	}

	public View getView() {
		return layout;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		imageButton.setBackgroundDrawable(null);
	}

	/**
	 * visibility: true / false [bool] (default: true) disable: true / false
	 * [bool] (default: false) opacity: 0.0～1.0 [float] (default: 1.0)
	 * backgroundColor: #000000 [string] (default: #000000) activeColor: #000000
	 * [string] (default: #0000FF) textColor: #000000 [string] (default:
	 * #FFFFFF) image: hoge.png (このファイルからみたときの相対パス) [string] text: テキスト [string]
	 */
	protected void style() {
		if (style.optString("image").length() > 0) {
			button.setVisibility(View.GONE);
			imageButton.setVisibility(View.VISIBLE);
			styleImageButton();
		} else {
			button.setVisibility(View.VISIBLE);
			imageButton.setVisibility(View.GONE);
			styleButton();
		}
	}

	protected void styleButton() {
		button.updateStyle(style);
		button.style();

		if (!style.optString("innerImage", "").equals("")) {
			ImageButton imageButton = button.getInnerImageButton();
			imageButton.setImageBitmap(context.readScaledBitmap(style
					.optString("innerImage")));
		}
	}

	protected void styleImageButton() {
		MyLog.e(TAG, "style image button");
		imageButton
				.setVisibility(style.optBoolean("visibility", true) ? View.VISIBLE
						: View.GONE);
		imageButton.setBackgroundColor(0);
		imageButton.setEnabled(!style.optBoolean("disable", false));

		Bitmap bitmap = context.readScaledBitmap(style.optString("image", ""));
		if (bitmap != null) {
			MyLog.e(TAG, "style. image.height:" + imageButton.getHeight());
			if (imageButton.getHeight() > 0) {
				int scaledHeight = imageButton.getHeight();
				bitmap = UIUtil.resizeBitmap(bitmap, scaledHeight);
			}
			Drawable drawable = new ImageButtonDrawable(
					new NonScaleBitmapDrawable(bitmap));

			imageButton.setBackgroundDrawable(drawable);
			imageButton.setPadding(0, 0, 0, 0);
		} else {
			imageButton.setBackgroundDrawable(null);
		}

	}

	@Override
	public void updateStyle(JSONObject update) {
		updateJSONObject(style, update);
		style();
	}

	public static class ButtonDrawable extends LayerDrawable {
		protected int backgroundColor, pressedBackgroundColor;

		private ButtonDrawable(Drawable drawable) {
			super(new Drawable[] { drawable });
		}

		@Override
		protected boolean onStateChange(int[] states) {
			for (int state : states) {
				if (state == android.R.attr.state_pressed) {
					super.setColorFilter(0x66000000, Mode.MULTIPLY);
				} else {
					super.clearColorFilter();
				}
			}
			return super.onStateChange(states);
		}

		@Override
		public boolean isStateful() {
			return true;
		}

	}

	public class ImageButtonDrawable extends StateListDrawable {
		protected int backgroundColor, pressedBackgroundColor;

		private ImageButtonDrawable(Drawable drawable) {
			super();
			Drawable pressed = new BitmapDrawable(context.getResources(),
					createBitmapWithColorFilter(drawable,
							new PorterDuffColorFilter(0x66000000,
									PorterDuff.Mode.MULTIPLY)));
			Drawable disabled = new BitmapDrawable(context.getResources(),
					createBitmapWithColorFilter(drawable,
							new PorterDuffColorFilter(0x66000000,
									PorterDuff.Mode.MULTIPLY)));

			addState(new int[] { android.R.attr.state_pressed }, pressed);
			addState(new int[] { -android.R.attr.state_enabled }, disabled);
			addState(new int[0], drawable.mutate());
		}
	}

	public class MonacaImageButton extends Button {

		public MonacaImageButton(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			Bitmap bitmap = context.readScaledBitmap(style.optString("image", ""));
			if (bitmap != null) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				int resolvedWidth = resolveSize(width, widthMeasureSpec);
				int resolvedHeight = resolveSize(height, heightMeasureSpec);

				MyLog.v(TAG, "bitmapW:" + width + ", bitmapH:" + height + ", resolvedW:" + resolvedWidth + ", resolvedH:" + resolvedHeight);
				setMeasuredDimension(resolvedWidth, resolvedHeight);
			}else{
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			MyLog.w(TAG, "onSizeChanged. w:" + w + ", h:" + h + ", oldw:" + oldw + ", oldh:" + oldh);
			resizeImage();
			super.onSizeChanged(w, h, oldw, oldh);
		}

		private void resizeImage() {
			Bitmap bitmap = context.readScaledBitmap(style.optString("image", ""));
			if (bitmap != null) {
				if (getMeasuredHeight() > 0) {
					int scaledHeight = getMeasuredHeight();
					bitmap = UIUtil.resizeBitmap(bitmap, scaledHeight);
				}
				Drawable drawable = new ImageButtonDrawable(
						new NonScaleBitmapDrawable(bitmap));

				imageButton.setBackgroundDrawable(drawable);
				imageButton.setPadding(0, 0, 0, 0);
			} else {
				imageButton.setBackgroundDrawable(null);
			}
		}
	}

	@Override
	public String[] getValidKeys() {
		return validKeys;
	}

	@Override
	public String getComponentName() {
		return "Button";
	}

	@Override
	public JSONObject getDefaultStyle() {
		return DefaultStyleJSON.button();
	}


}
