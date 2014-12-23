package activity;

import receiver.AutoUpdateReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjt.cuteweather.R;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout tmpInfoLayout;
	private LinearLayout weatherInfoLayout;
	private TextView countryNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		tmpInfoLayout = (LinearLayout) findViewById(R.id.tmp_info_layout);
		countryNameText = (TextView) findViewById(R.id.country_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		if (getIntent().getBooleanExtra("city_selected", false)
				|| getIntent().getBooleanExtra("isFromWeatherActivity", false)) {
			countryNameText.setText(prefs.getString("city_name", ""));
			publishText.setText("今天" + prefs.getString("publish_time", "")
					+ "发布");
			currentDateText.setText(prefs.getString("current_date", ""));
			weatherDespText.setText(prefs.getString("weather_desp", ""));
			temp1.setText(prefs.getString("temp1", ""));
			temp2.setText(prefs.getString("temp2", ""));
			return;
		}

		String countryCode = getIntent().getStringExtra("country_code");
		String countryName = getIntent().getStringExtra("country_name");
		if (!TextUtils.isEmpty(countryCode)) {
			publishText.setText("正在同步...");
			countryNameText.setText(countryName);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			tmpInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}
	}

	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("同步失败。");
					}
				});
			}
		});
	}

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		countryNameText.setText(prefs.getString("city_name", ""));
		temp1.setText(prefs.getString("temp1", ""));
		temp2.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		tmpInfoLayout.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateReceiver.class);
		startService(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("正在刷新...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}
}
