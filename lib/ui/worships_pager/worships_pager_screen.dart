import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_cubit.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';

class WorshipsPagerScreen extends StatefulWidget {
  const WorshipsPagerScreen({super.key});

  @override
  State<WorshipsPagerScreen> createState() => _WorshipsPagerScreenState();
}

class _WorshipsPagerScreenState extends State<WorshipsPagerScreen> {
  int _pageIndex = 0;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return BlocBuilder<WorshipsPagerCubit, WorshipsPagerState>(
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
