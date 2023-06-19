import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/data/local/database.dart';
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
        RepositoryProvider<AppDatabase>(
          create: (context) => AppDatabase(),
        ),
        RepositoryProvider<HttpClients>(
          create: (context) => HttpClients(),
        ),
        RepositoryProvider<EventsRepository>(
          create: (context) => EventsRepositoryImpl(
            database: context.read(),
            eventsService: context.read<HttpClients>().geth.getService(),
          ),
        ),
      ],
      child: child,
    );
  }
}
