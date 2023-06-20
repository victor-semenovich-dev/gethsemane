import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worships/worships_cubit.dart';
import 'package:gethsemane/ui/worships/worships_state.dart';

class WorshipsScreen extends StatefulWidget {
  const WorshipsScreen({super.key});

  @override
  State<WorshipsScreen> createState() => _WorshipsScreenState();
}

class _WorshipsScreenState extends State<WorshipsScreen> {
  int _pageIndex = 0;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return BlocBuilder<WorshipsCubit, WorshipsState>(
      builder: (context, state) {
        return Scaffold(
          appBar: AppBar(
            backgroundColor: colorScheme.primary,
            title: Text(
              state.worshipEvents.isNotEmpty
                  ? state.worshipEvents[_pageIndex].title
                  : '',
              style: TextStyle(color: colorScheme.onPrimary),
            ),
          ),
          body: PageView(
            onPageChanged: (i) => setState(() => _pageIndex = i),
            children: state.worshipEvents
                .map(
                  (e) => Center(
                    child: Text(
                      e.id.toString(),
                      style: const TextStyle(fontSize: 40),
                    ),
                  ),
                )
                .toList(),
          ),
        );
      },
    );
  }
}
