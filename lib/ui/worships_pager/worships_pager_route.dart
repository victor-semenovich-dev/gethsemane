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
  int _pageIndex = 0;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      context.read<WorshipsPagerCubit>().syncEvents();
    }
  }

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
                  ? state.worshipEvents[_pageIndex].date.eventTitle(context)
                  : '',
              style: TextStyle(color: colorScheme.onPrimary),
            ),
            actions: [
              if (state.isInProgress) _actionProgress(),
            ],
            centerTitle: false,
          ),
          body: _body(state),
        );
      },
    );
  }

  Widget _body(WorshipsPagerState state) {
    if (state.isError && state.worshipEvents.isEmpty) {
      return RetryWidget(
        onRetryClick: () => context.read<WorshipsPagerCubit>().syncEvents(),
      );
    } else {
      return PageView(
        onPageChanged: (i) => setState(() => _pageIndex = i),
        children: state.worshipEvents
            .map((e) => WorshipPageProvider(id: e.id))
            .toList(),
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
