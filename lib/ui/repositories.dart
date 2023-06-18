import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/data/remote/client.dart';
import 'package:gethsemane/data/repository/events.dart';
import 'package:gethsemane/domain/repository/events.dart';

class RepositoriesProvider extends StatelessWidget {
  final Widget child;

  const RepositoriesProvider({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return MultiRepositoryProvider(
      providers: [
        RepositoryProvider<EventsRepository>(
          create: (context) => EventsRepositoryImpl(gethClient),
        ),
      ],
      child: child,
    );
  }
}
