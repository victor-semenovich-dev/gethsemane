import 'dart:async';
import 'dart:convert';

import 'package:chopper/chopper.dart';

class GethAuthenticator extends Authenticator {
  @override
  FutureOr<Request?> authenticate(Request request, Response<dynamic> response,
      [Request? originalRequest]) {
    const username = String.fromEnvironment('API_GETH_BASIC_AUTH_USERNAME');
    const password = String.fromEnvironment('API_GETH_BASIC_AUTH_PASSWORD');
    final basicAuth =
        'Basic ${base64Encode(utf8.encode('$username:$password'))}';
    final headers = Map<String, String>.of(request.headers);
    headers['Authorization'] = basicAuth;
    return request.copyWith(headers: headers);
  }
}
