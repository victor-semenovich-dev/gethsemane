import 'package:chopper/chopper.dart';
import 'package:gethsemane/data/remote/auth/geth_auth.dart';

final gethClient = ChopperClient(
  baseUrl: Uri.parse('http://api.geth.by'),
  services: [],
  authenticator: GethAuthenticator(),
  converter: const JsonConverter(),
  interceptors: [HttpLoggingInterceptor()],
);
