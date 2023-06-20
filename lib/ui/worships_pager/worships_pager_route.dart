import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worship/worship_page_provider.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_cubit.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';

class WorshipsPagerRoute extends StatefulWidget {
  const WorshipsPagerRoute({super.key});

  @override
  State<WorshipsPagerRoute> createState() => _WorshipsPagerRouteState();
}

class _WorshipsPagerRouteState extends State<WorshipsPagerRoute> {
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
                .map((e) => WorshipPageProvider(id: e.id))
                .toList(),
          ),
        );
      },
    );
  }
}
