package mobi.monaca.framework.nativeui;

import mobi.monaca.framework.nativeui.component.Component;
import mobi.monaca.framework.nativeui.exception.NativeUIException;

import org.json.JSONObject;

import android.view.View;

public class UIEventer extends Component{
    protected JSONObject event;
    protected UIContext context;
    protected static final String[] validKeys = {
	"onTapBackButton",
	"onTap"
    };

    public UIEventer(UIContext context, JSONObject event) throws NativeUIException {
	super(event);
        this.context = context;
        this.event = event;
    }

    public boolean hasOnTapBackButtonAction() {
        return !event.optString("onTapBackButton", "").equals("");
    }

    public void onTapBackButton() {
        String code = event.optString("onTapBackButton", "");

        if (!code.equals("")) {
            context.react("javascript:" + code);
        }
    }

	@Override
	public String getComponentName() {
		return "event";
	}

	@Override
	public String[] getValidKeys() {
		return validKeys;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStyle(JSONObject update) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject getDefaultStyle() {
		return null;
	}

}
