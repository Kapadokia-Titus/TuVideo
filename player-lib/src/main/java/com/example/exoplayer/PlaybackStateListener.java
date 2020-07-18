package com.example.exoplayer;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

import static android.content.ContentValues.TAG;

public class PlaybackStateListener implements Player.EventListener {

    /**
     *
     * onPlayerStateChanged is called when:
             * play/pause state changes, given by the playWhenReady parameter
             * playback state changes, given by the playbackState parameter
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        String stateString;
        switch (playbackState) {
            case ExoPlayer.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -"; //The player has been instantiated but has not yet been prepared with a MediaSource.
                break;
            case ExoPlayer.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";// The player is not able to play from the current position because not enough data has been buffered.
                break;
            case ExoPlayer.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -"; //The player is able to immediately play from the current position
                break;
            case ExoPlayer.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -"; // The player has finished playing the media.
                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }
        Log.d(TAG, "changed state to " + stateString
                + " playWhenReady: " + playWhenReady);

    }
}
