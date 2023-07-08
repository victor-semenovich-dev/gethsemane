import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/extensions/datetime.dart';
import 'package:gethsemane/ui/common/retry_widget.dart';
import 'package:gethsemane/ui/worship/worship_page_provider.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_cubit.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_state.dart';

class WorshipsPagerRoute extends StatefulWidget {
  const WorshipsPagerRoute({super.key});

  @override
  State<WorshipsPagerRoute> createState() => _WorshipsPagerRouteState();
}

class _WorshipsPagerRouteState extends State<WorshipsPagerRoute>
    with WidgetsBindingObserver {
  int? _currentEventId;
  late PageController _pageController;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    _pageController = PageController();
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _pageController.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      context.read<WorshipsPagerCubit>().reloadEvents();
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return BlocConsumer<WorshipsPagerCubit, WorshipsPagerState>(
      listener: (context, state) {
        final pageIndex = (_pageController.page ?? 0).round();
        // If the current is the first page, stay on the first page.
        // Otherwise, try to stay on the current event page.
        // If the current event is not found, jump to the first page.
        if (pageIndex > 0 &&
            (pageIndex >= state.worshipEvents.length ||
                state.worshipEvents[pageIndex].id != _currentEventId)) {
          final correctIndex = max(
              state.worshipEvents
                  .indexWhere((event) => event.id == _currentEventId),
              0);
          debugPrint('correct index - $correctIndex');
          _pageController.jumpToPage(correctIndex);
        }
      },
      listenWhen: (WorshipsPagerState previous, WorshipsPagerState current) {
        return !listEquals(previous.worshipEvents, current.worshipEvents);
      },
      builder: (context, state) {
        return Scaffold(
          appBar: AppBar(
            backgroundColor: colorScheme.primary,
            // The FutureBuilder is needed to build the Text title widget after the PageView has been built.
            // So we will be able to use the _pageController.
            title: FutureBuilder(
              future: Future.value(true),
              builder: (context, snapshot) {
                final pageIndex = (_pageController.page ?? 0).round();
                return Text(
                  state.worshipEvents.isNotEmpty
                      ? state.worshipEvents[pageIndex].date.eventTitle(context)
                      : '',
                  style: TextStyle(color: colorScheme.onPrimary),
                );
              },
            ),
            actions: [
              if (state.isInProgress) _actionProgress(),
            ],
            centerTitle: false,
          ),
          body: _body(),
        );
      },
    );
  }

  Widget _body() {
    final cubit = context.read<WorshipsPagerCubit>();
    if (cubit.state.isError && cubit.state.worshipEvents.isEmpty) {
      return RetryWidget(
        onRetryClick: () => context.read<WorshipsPagerCubit>().reloadEvents(),
      );
    } else {
      return PageView.builder(
        itemCount: cubit.state.worshipEvents.length,
        controller: _pageController,
        itemBuilder: (BuildContext context, int index) {
          final event = cubit.state.worshipEvents[index];
          return WorshipPageProvider(
            key: ValueKey(event.id),
            id: event.id,
          );
        },
        onPageChanged: (i) {
          if (cubit.state.worshipEvents.length - i <
                  WorshipsPagerCubit.loadMoreEventsThreshold &&
              !cubit.state.isInProgress) {
            context.read<WorshipsPagerCubit>().loadMoreEvents();
          }
          setState(() {
            _currentEventId = cubit.state.worshipEvents[i].id;
          });
        },
      );
    }
  }

  Widget _actionProgress() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: SizedBox(
        width: 24,
        height: 24,
        child: CircularProgressIndicator(
          color: Theme.of(context).colorScheme.onPrimary,
          strokeWidth: 2,
        ),
      ),
    );
  }
}
