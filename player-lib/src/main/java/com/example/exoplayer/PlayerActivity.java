/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.exoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;
  private PlaybackStateListener playbackStateListener;

  private static final String TAG = PlayerActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    playerView = findViewById(R.id.video_view);
    playbackStateListener = new PlaybackStateListener();
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

//  using adaptive streaming
  //a DefaultTrackSelector  is responsible for choosing tracks in the media source.
  // then trackSelector is set to only pick standard definition or lower
  //  - a good way of saving our user's data at the expense of quality. Lastly,
//  we pass our trackSelector to our builder so that it is used when building the SimpleExoPlayer instance.
  private void initializePlayer() {


    if (player == null) {
      DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
      trackSelector.setParameters(
              trackSelector.buildUponParameters().setMaxVideoSizeSd());
      player = new SimpleExoPlayer.Builder(this)
              .setTrackSelector(trackSelector)
              .build();
    }
    playerView.setPlayer(player);

    // change our URI to one which points to a DASH media source:
    Uri uri = Uri.parse(getString(R.string.media_url_dash));
    player.addListener(playbackStateListener);
    MediaSource mediaSource = buildMediaSource(uri);
    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    player.prepare(mediaSource, false, false);
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.removeListener(playbackStateListener);
      player.release();
      player = null;
    }
  }

  // we start with progressive media source for the Mp3 file on the internet

  /**
   *
   * @param uri  contains the URI of the media file
   * @return
   * DefaultDataSourceFactory, specifies our context and user-agent-string which will be used when making
   * the HTTP request for the media file.
   * we then pass dataSourceFactory to ProgressiveMediaSource.Factory, which is a factory for creating a progressive media
   *
   * Multimedia data is usually stored using a container format,
   * such as MP4 or Ogg. Before the video and/or audio data can be played it must be
   * extracted from the container. ExoPlayer is capable of extracting data from many different
   * container formats using a variety of Extractor classes.
   *
   *
   *
   *
   * DASH is a widely used adaptive streaming format. To stream DASH content we need to create a DashMediaSource.
   */

  /**
   *
   * We are now streaming DASH and using available bandwidth to selecting tracks. To see this in action you can:
   *
   * Tap double shift to open up Search Everywhere
   * Find the AdaptiveTrackSelection class
   * In the class find updateSelectedTrack - this method will be called when the track is updated
   * Put a breakpoint on the line if (selectedIndex == currentSelectedIndex
   * Start your app in debug mode.
   * Watch how the breakpoint is hit each time the track selector needs to decide which track to use.
   */
  private MediaSource buildMediaSource(Uri uri) {
    DataSource.Factory dataSourceFactory =
            new DefaultDataSourceFactory(this, "exoplayer-codelab");
    DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
    return mediaSourceFactory.createMediaSource(uri);
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }
}
