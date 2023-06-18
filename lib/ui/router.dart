import 'package:gethsemane/ui/worships/worships_route.dart';
import 'package:go_router/go_router.dart';

final router = GoRouter(
  initialLocation: '/worships',
  routes: [
    GoRoute(
      path: '/worships',
      builder: (context, state) => const WorshipsRoute(),
    ),
  ],
);
