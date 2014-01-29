package tr.com.serayuzgur.runlight;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class QuestionDialog extends AlertDialog {

    public QuestionDialog(Context context) {
        this(context, R.string.yes, R.string.no);
    }


    public QuestionDialog(Context context, int positiveText, int negativeText) {
        super(context);
        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(positiveText), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPositive();
            }
        });
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(negativeText), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onNegative();
            }
        });


    }

    protected void onNegative() {
    	dismiss();
    }

    protected void onPositive() {
    	dismiss();
    }

}
