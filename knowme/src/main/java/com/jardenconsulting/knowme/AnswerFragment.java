package com.jardenconsulting.knowme;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AnswerFragment extends Fragment {
	
	private TextView hisQuestionView;
	private ImageView myBallRight;
	private ImageView myBallWrong;
	private TextView myHimResult1View;
	private TextView myHimView;
	private TextView myHimResult2View;
	private TextView hisHimView;

	private TextView myQuestionView;
	private ImageView hisBallRight;
	private ImageView hisBallWrong;
	private TextView hisMeResult1View;
	private TextView hisMeView;
	private TextView hisMeResult2View;
	private TextView myMeView;
	
	private String meRightTemplate;
	private String meWrongTemplate;
	private String meWrong2Template;
	private String himRightTemplate;
	private String himWrongTemplate;
	private String meRightStr;
	private String meWrongStr;
	private String meWrong2Str;
	private String himRightStr;
	private String himWrongStr;
	private String himWrong2Str;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_answer, container,
				false);
		this.hisQuestionView = (TextView) rootView.findViewById(R.id.hisQuestionView);
		this.myBallRight = (ImageView) rootView.findViewById(R.id.myBallRight);
		this.myBallWrong = (ImageView) rootView.findViewById(R.id.myBallWrong);
		this.myHimResult1View = (TextView) rootView.findViewById(R.id.myHimResult1View);
		this.myHimView = (TextView) rootView.findViewById(R.id.myHimView);
		this.myHimResult2View = (TextView) rootView.findViewById(R.id.myHimResult2View);
		this.hisHimView = (TextView) rootView.findViewById(R.id.hisHimView);

		this.myQuestionView = (TextView) rootView.findViewById(R.id.myQuestionView);
		this.hisBallRight = (ImageView) rootView.findViewById(R.id.hisBallRight);
		this.hisBallWrong = (ImageView) rootView.findViewById(R.id.hisBallWrong);
		this.hisMeResult1View = (TextView) rootView.findViewById(R.id.hisMeResult1View);
		this.hisMeView = (TextView) rootView.findViewById(R.id.hisMeView);
		this.hisMeResult2View = (TextView) rootView.findViewById(R.id.hisMeResult2View);
		this.myMeView = (TextView) rootView.findViewById(R.id.myMeView);
		
		Resources resources = getResources();
		this.meRightTemplate = resources.getString(R.string.meRightTemplate);
		this.meWrongTemplate = resources.getString(R.string.meWrongTemplate);
		this.meWrong2Template = resources.getString(R.string.meWrong2Template);
		this.himRightTemplate = resources.getString(R.string.himRightTemplate);
		this.himWrongTemplate = resources.getString(R.string.himWrongTemplate);
		this.himWrong2Str = resources.getString(R.string.himWrong2Str);

		return rootView;
	}

	public void displayResults(String[] questionArray, int myMe, int myHim,
			int hisHim, int hisMe) {
		this.hisQuestionView.setText(questionArray[1]);
		if (hisHim == -1) { // i.e. meRight
			this.myBallWrong.setVisibility(View.GONE);
			this.myBallRight.setVisibility(View.VISIBLE);
			this.myHimResult1View.setText(this.meRightStr);
			this.myHimView.setText(questionArray[myHim+1]);
			this.myHimResult2View.setVisibility(View.GONE);
			this.hisHimView.setVisibility(View.GONE);
		} else {
			this.myBallWrong.setVisibility(View.VISIBLE);
			this.myBallRight.setVisibility(View.GONE);
			this.myHimResult1View.setText(this.meWrongStr);
			this.myHimView.setText(questionArray[myHim+1]);
			this.myHimResult2View.setText(this.meWrong2Str);
			this.myHimResult2View.setVisibility(View.VISIBLE);
			this.hisHimView.setText(questionArray[hisHim+1]);
			this.hisHimView.setVisibility(View.VISIBLE);
		}

		this.myQuestionView.setText(questionArray[0]);
		if (hisMe == -1) { // i.e. himRight
			this.hisBallWrong.setVisibility(View.GONE);
			this.hisBallRight.setVisibility(View.VISIBLE);
			this.hisMeResult1View.setText(this.himRightStr);
			this.hisMeView.setText(questionArray[myMe+1]);
			this.hisMeResult2View.setVisibility(View.GONE);
			this.myMeView.setVisibility(View.GONE);
		} else {
			this.hisBallWrong.setVisibility(View.VISIBLE);
			this.hisBallRight.setVisibility(View.GONE);
			this.hisMeResult1View.setText(this.himWrongStr);
			this.hisMeView.setText(questionArray[hisMe+1]);
			this.hisMeResult2View.setText(this.himWrong2Str);
			this.hisMeResult2View.setVisibility(View.VISIBLE);
			this.myMeView.setText(questionArray[myMe+1]);
			this.myMeView.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void setOtherPlayerName(String otherPlayerName) {
		this.meRightStr = this.meRightTemplate.replace("{}", otherPlayerName);
		this.meWrongStr = this.meWrongTemplate.replace("{}", otherPlayerName);
		this.meWrong2Str = this.meWrong2Template.replace("{}", otherPlayerName);
		this.himRightStr = this.himRightTemplate.replace("{}", otherPlayerName);
		this.himWrongStr = this.himWrongTemplate.replace("{}", otherPlayerName);
	}
}
