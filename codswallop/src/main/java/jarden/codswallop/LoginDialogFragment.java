package jarden.codswallop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

/**
 * Created by john.denny@gmail.com on 09/01/2026.
 */
public class LoginDialogFragment extends DialogFragment implements View.OnClickListener {
    public interface LoginDialogListener {
        public void onHostButton(String playerName);
        public void onJoinButton(String playerName);
    }
    private static final String PLAYER_NAME_KEY = "PLAYER_NAME_KEY";
    private LoginDialogListener loginDialogListener;
    private static final String TAG = "LoginDialogFragment";
    private EditText playerNameEditText;
    private Button hostButton;
    private Button joinButton;
    private AlertDialog alertDialog;
    private SharedPreferences sharedPreferences;
    private String previousPlayerName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginDialogListener = (LoginDialogListener) context;
        sharedPreferences = context.getSharedPreferences(GameActivity.TAG, Context.MODE_PRIVATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        previousPlayerName = sharedPreferences.getString(PLAYER_NAME_KEY, "");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_login, null);
        playerNameEditText = view.findViewById(R.id.nameEditText);
        playerNameEditText.setText(previousPlayerName);
        hostButton = view.findViewById(R.id.hostButton);
        hostButton.setOnClickListener(this);
        joinButton = view.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(this);
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }
    @Override
    public void onClick(View view) {
        String playerName = playerNameEditText.getText().toString();
        if (playerName.isEmpty()) {
            Toast.makeText(getContext(), "Supply your name first!", Toast.LENGTH_LONG).show();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "playerName=" + playerName);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PLAYER_NAME_KEY, playerName);
            editor.apply();
            int viewId = view.getId();
            if (viewId == R.id.hostButton) {
                loginDialogListener.onHostButton(playerName);
            } else if (viewId == R.id.joinButton) {
                loginDialogListener.onJoinButton(playerName);
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "unrecognised button, viewId=" + viewId);
                }
            }
            alertDialog.cancel();
        }
    }
}

// somewhere! LoginDialogFragment().show(supportFragmentManager, "LOGIN_DIALOG");
