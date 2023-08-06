import 'dart:async';

import 'package:chopper/chopper.dart';

class GethsemaneAuthInterceptor implements RequestInterceptor {
  @override
  FutureOr<Request> onRequest(Request request) {
    const xApiKey = String.fromEnvironment('API_GETHSEMANE_X_API_KEY');
    final headers = Map<String, String>.of(request.headers);
    headers['X-Api-Key'] = xApiKey;
    return request.copyWith(headers: headers);
  }
}
