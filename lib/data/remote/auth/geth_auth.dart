import 'dart:async';
import 'dart:convert';

import 'package:chopper/chopper.dart';

class GethAuthInterceptor extends RequestInterceptor {
  @override
  FutureOr<Request> onRequest(Request request) {
    const username = String.fromEnvironment('API_GETH_BASIC_AUTH_USERNAME');
    const password = String.fromEnvironment('API_GETH_BASIC_AUTH_PASSWORD');
    final basicAuth =
        'Basic ${base64Encode(utf8.encode('$username:$password'))}';
    final headers = Map<String, String>.of(request.headers);
    headers['Authorization'] = basicAuth;
    return request.copyWith(headers: headers);
  }
}
