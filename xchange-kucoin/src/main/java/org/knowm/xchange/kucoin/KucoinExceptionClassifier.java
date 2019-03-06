package org.knowm.xchange.kucoin;

import com.kucoin.sdk.exception.KucoinApiException;
import java.io.IOException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.ExchangeSecurityException;
import org.knowm.xchange.exceptions.ExchangeUnavailableException;
import org.knowm.xchange.exceptions.NonceException;

public final class KucoinExceptionClassifier {

  KucoinExceptionClassifier() {}

  public static <T> T classifyingExceptions(IOExceptionThrowingSupplier<T> apiCall)
      throws IOException {
    try {
      T result = apiCall.get();
      if (result == null) {
        throw new ExchangeException("Empty response from Kucoin. Check logs.");
      }
      return result;
    } catch (KucoinApiException e) {
      throw classify(e);
    }
  }

  public static RuntimeException classify(KucoinApiException e) {
    if (e.getMessage().equalsIgnoreCase("Service unavailable")) {
      return new ExchangeUnavailableException(e.getMessage(), e);
    }
    if (e.getMessage().contains("check environment variables")) {
      return new ExchangeSecurityException(e.getMessage(), e);
    }

    // TODO need to handle HTTP error codes which aren't covered
    // by getCode(), but it doesn't seem to be possible to trigger
    // these deliberately; Kucoin's API seems to block requests
    // down to way below the permitted rates anyway.
    // case "401":
    // case "402":
    // case "429": return new RateLimitExceededException(e.getMessage(), e);
    // case "503": return new SystemOverloadException(e.getMessage(), e);

    switch (e.getCode()) {
      case "400001":
      case "400003":
      case "400004":
      case "400006":
      case "400007":
      case "411100":
        return new ExchangeSecurityException(e.getMessage(), e);
      case "400005":
        return new NonceException(e.getMessage(), e);
      default:
        return new ExchangeException(e.getMessage(), e);
    }
  }

  @FunctionalInterface
  interface IOExceptionThrowingSupplier<T> {
    T get() throws IOException;
  }
}
