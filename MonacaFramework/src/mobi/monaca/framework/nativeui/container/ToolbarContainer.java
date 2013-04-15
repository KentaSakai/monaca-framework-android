package mobi.monaca.framework.nativeui.container;

import static mobi.monaca.framework.nativeui.UIUtil.buildColor;
import static mobi.monaca.framework.nativeui.UIUtil.buildOpacity;
import static mobi.monaca.framework.nativeui.UIUtil.updateJSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.monaca.framework.nativeui.ComponentEventer;
import mobi.monaca.framework.nativeui.DefaultStyleJSON;
import mobi.monaca.framework.nativeui.NonScaleBitmapDrawable;
import mobi.monaca.framework.nativeui.UIContext;
import mobi.monaca.framework.nativeui.UIUtil;
import mobi.monaca.framework.nativeui.component.BackButtonComponent;
import mobi.monaca.framework.nativeui.component.ButtonComponent;
import mobi.monaca.framework.nativeui.component.Component;
import mobi.monaca.framework.nativeui.component.LabelComponent;
import mobi.monaca.framework.nativeui.component.SearchBoxComponent;
import mobi.monaca.framework.nativeui.component.SegmentComponent;
import mobi.monaca.framework.nativeui.component.ToolbarBackgroundDrawable;
import mobi.monaca.framework.nativeui.component.ToolbarComponent;
import mobi.monaca.framework.nativeui.component.view.ContainerShadowView;
import mobi.monaca.framework.nativeui.exception.InvalidValueException;
import mobi.monaca.framework.nativeui.exception.NativeUIException;
import mobi.monaca.framework.psedo.R;
import mobi.monaca.framework.util.MyLog;

import org.json.JSONArray;
import org.json.JSONObject;
import static mobi.monaca.framework.nativeui.UIUtil.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class ToolbarContainer extends Container {
	protected UIContext context;
	protected ToolbarContainerView view;
	protected ToolbarComponent left, center, right;
	protected AlphaAnimation animation = null;
	private ContainerShadowView shadowView;
	protected static final int mContainerViewID = 1001;

	protected static String[] validKeys = {
		"container",
		"style",
		"iosStyle",
		"androidStyle",
		"id",
		"left",
		"center",
		"right"
	};

	protected static String[] validComponents = {"backButton",
												"button",
												"searchBox",
												"label",
												"segment"
	};


	public ToolbarContainer(UIContext context, JSONObject toolbarJSON, boolean isTop) throws NativeUIException {
		super(toolbarJSON);
		this.context = context;
		view = new ToolbarContainerView(context, isTop);
		view.setId(mContainerViewID);
		shadowView = new ContainerShadowView(context, isTop);

		buildChildren();
		style();
	}

	private void buildChildren() throws NativeUIException {
		JSONArray left = getComponentJSON().optJSONArray("left");
		if (left != null) {
			ArrayList<ToolbarComponent> leftComponents = buildComponents(left);
			view.setLeftView(leftComponents);
		}
		JSONArray right = getComponentJSON().optJSONArray("right");
		if (right != null) {
			ArrayList<ToolbarComponent> rightComponents = buildComponents(right);
			view.setRightView(rightComponents);
		}

		JSONArray center = getComponentJSON().optJSONArray("center");
		if (center != null) {
			ArrayList<ToolbarComponent> centerComponents = buildComponents(center);
			boolean shouldExpandItemWidth = false;
			if( (left == null && right == null) || (left == null && right.length() == 0) || (left.length() == 0 && right == null) || (left.length() == 0 && right.length() == 0 ) ){
				shouldExpandItemWidth = true;
			}
			view.setCenterView(centerComponents, shouldExpandItemWidth);
		}
	}

	private ArrayList<ToolbarComponent> buildComponents(JSONArray left) throws NativeUIException {
		ArrayList<ToolbarComponent> leftComponents = new ArrayList<ToolbarComponent>();
		ToolbarComponent component;
		JSONObject componentJSON;
		for (int i = 0; i < left.length(); i++) {
			componentJSON = left.optJSONObject(i);
			component = buildComponent(componentJSON);
			leftComponents.add(component);
		}
		return leftComponents;
	}

	private ToolbarComponent buildComponent(JSONObject childJSON) throws NativeUIException{
		String componentType = childJSON.optString("component");
		if (componentType.equals("backButton")) {
			return new BackButtonComponent(context, childJSON);
		} else if (componentType.equals("button")) {
			return new ButtonComponent(context, childJSON);
		} else if (componentType.equals("searchBox")) {
			return new SearchBoxComponent(context, childJSON);
		} else if (componentType.equals("label")) {
			return new LabelComponent(context, childJSON);
		} else if (componentType.equals("segment")) {
			return new SegmentComponent(context, childJSON);
		}else{
			throw new InvalidValueException("Toolbar", "component", componentType, validComponents);
		}
	}

	public void updateStyle(JSONObject update) {
		updateJSONObject(style, update);
		style();
	}

	public View getView() {
		return view;
	}

	/**
	 * visibility: [bool] (default: true) opacity: 0.0-1.0 [float] (default:
	 * 1.0) backgroundColor: #000000 [string] (default: undefined) position :
	 * "fixed" | "scroll" (default: "fixed") => androidだと無理ぽい title : [string]
	 * (default : "") (このスタイルが指定された場合、center属性は無視される) titleImage : [string]
	 * (default : "") このスタイルが指定された時、center属性は無視)
	 */
	protected void style() {
		double toolbarOpacity = style.optDouble("opacity", 1.0);
		if (isTransparent() && view.getVisibility() != (style.optBoolean("visibility", true) ? View.VISIBLE : View.INVISIBLE)) {
			if (animation != null) {
				// animation.cancel(); //TODO only available in Android 4.0
			}

			animation = style.optBoolean("visibility", true) ? new AlphaAnimation(0f, 1.0f) : new AlphaAnimation(1.0f, 0f);

			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					view.setVisibility(style.optBoolean("visibility", true) ? View.VISIBLE : View.INVISIBLE);
					ToolbarContainer.this.animation = null;
				}
			});
			animation.setInterpolator(new LinearInterpolator());
			animation.setDuration(200);

			// cause GC to prevent "stop the world" on animation.
			System.gc();

			view.startAnimation(animation);
		} else {
			view.setVisibility(style.optBoolean("visibility", true) ? View.VISIBLE : View.GONE);
		}

		/*
		 * view.setTitleSubtitle(style.optString("title"),
		 * style.optString("subtitle"));
		 */

		// titleColor
		view.setTitleColor(style.optString("titleColor", "#ffffff"));

		// subtitleColor
		view.setSubtitleColor(style.optString("subtitleColor", "#ffffff"));

		// titleFontScale
		view.setTitleFontScale(style.optString("titleFontScale", ""));

		// subtitleFontScale
		view.setSubitleFontScale(style.optString("subtitleFontScale", ""));

		String titleImagePath = style.optString("titleImage", "");
		view.setTitleSubtitle(style.optString("title"), style.optString("subtitle"),
				titleImagePath.equals("") ? null : context.readScaledBitmap(titleImagePath));

		ColorFilter filter = new PorterDuffColorFilter(buildColor(style.optString("backgroundColor", "#ff0000")), PorterDuff.Mode.SCREEN);

		Drawable toolbarBackground = new ToolbarBackgroundDrawable(context);
		toolbarBackground.setColorFilter(filter);
		toolbarBackground.setAlpha(buildOpacity(style.optDouble("opacity", 1.0)));

		view.getContentView().setBackgroundDrawable(toolbarBackground);

		double shadowOpacity = style.optDouble("shadowOpacity", 0.3);
		double relativeShadowOpacity = toolbarOpacity * shadowOpacity;
		shadowView.getBackground().setAlpha(buildOpacity(relativeShadowOpacity));

		view.setBackgroundDrawable(null);
		view.setBackgroundColor(0);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view.requestFocus();
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		view.getContentView().setBackgroundDrawable(null);
	}

	public boolean isTransparent() {
		double opacity = style.optDouble("opacity", 1.0);
		return opacity <= 0.999;
	}

	public View getShadowView() {
		return shadowView;
	}

	@Override
	public String getComponentName() {
		return "Toolbar";
	}

	@Override
	public JSONObject getDefaultStyle() {
		return DefaultStyleJSON.toolbar();
	}

	@Override
	public String[] getValidKeys() {
		return validKeys;
	}
}
