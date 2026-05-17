import 'package:flutter/material.dart';
import 'package:supplement_routine/app/app_layout.dart';
import 'package:supplement_routine/l10n/generated/app_localizations.dart';

import 'today/today_screen.dart';
import 'supplement/supplement_list_screen.dart';
import 'history/history_screen.dart';
import 'settings/settings_screen.dart';

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({super.key});

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _selectedIndex = 0;

  final List<Widget?> _screens = [const TodayScreen(), null, null, null];

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final destinations = [
      _NavigationDestinationData(
        icon: Icons.today_outlined,
        selectedIcon: Icons.today,
        label: l10n.navToday,
      ),
      _NavigationDestinationData(
        icon: Icons.medication_outlined,
        selectedIcon: Icons.medication,
        label: l10n.navSupplements,
      ),
      _NavigationDestinationData(
        icon: Icons.calendar_month_outlined,
        selectedIcon: Icons.calendar_month,
        label: l10n.navHistory,
      ),
      _NavigationDestinationData(
        icon: Icons.settings_outlined,
        selectedIcon: Icons.settings,
        label: l10n.navSettings,
      ),
    ];

    return LayoutBuilder(
      builder: (context, constraints) {
        final useNavigationRail =
            constraints.maxWidth >= AppLayout.expandedBreakpoint;

        if (useNavigationRail) {
          return Scaffold(
            body: Row(
              children: [
                NavigationRail(
                  selectedIndex: _selectedIndex,
                  labelType: NavigationRailLabelType.all,
                  onDestinationSelected: _selectDestination,
                  destinations: destinations
                      .map(
                        (destination) => NavigationRailDestination(
                          icon: Icon(destination.icon),
                          selectedIcon: Icon(destination.selectedIcon),
                          label: Text(destination.label),
                        ),
                      )
                      .toList(),
                ),
                const VerticalDivider(width: 1),
                Expanded(
                  child: IndexedStack(
                    index: _selectedIndex,
                    children: _stackChildren,
                  ),
                ),
              ],
            ),
          );
        }

        return Scaffold(
          body: IndexedStack(index: _selectedIndex, children: _stackChildren),
          bottomNavigationBar: NavigationBar(
            selectedIndex: _selectedIndex,
            onDestinationSelected: _selectDestination,
            destinations: destinations
                .map(
                  (destination) => NavigationDestination(
                    icon: Icon(destination.icon),
                    selectedIcon: Icon(destination.selectedIcon),
                    label: destination.label,
                  ),
                )
                .toList(),
          ),
        );
      },
    );
  }

  void _selectDestination(int index) {
    setState(() {
      _selectedIndex = index;
      _screens[index] ??= _buildScreen(index);
    });
  }

  List<Widget> get _stackChildren {
    return _screens.map((screen) => screen ?? const SizedBox.shrink()).toList();
  }

  Widget _buildScreen(int index) {
    return switch (index) {
      0 => const TodayScreen(),
      1 => const SupplementListScreen(),
      2 => const HistoryScreen(),
      3 => const SettingsScreen(),
      _ => const SizedBox.shrink(),
    };
  }
}

class _NavigationDestinationData {
  const _NavigationDestinationData({
    required this.icon,
    required this.selectedIcon,
    required this.label,
  });

  final IconData icon;
  final IconData selectedIcon;
  final String label;
}
