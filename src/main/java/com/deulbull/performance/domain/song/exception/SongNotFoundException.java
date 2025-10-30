package com.deulbull.performance.domain.song.exception;

import com.deulbull.performance.global.exception.BaseException;

public class SongNotFoundException extends BaseException {
  public SongNotFoundException() {
    super(SongErrorCode.SONG_404_NOT_FOUND);
  }
}
