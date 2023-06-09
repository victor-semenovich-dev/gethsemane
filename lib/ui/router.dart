import 'package:gethsemane/ui/worships_pager/worships_pager_provider.dart';
import 'package:go_router/go_router.dart';

final router = GoRouter(
  initialLocation: '/worships',
  routes: [
    GoRoute(
      path: '/worships',
      builder: (context, state) => const WorshipsPagerProvider(),
    ),
  ],
);
