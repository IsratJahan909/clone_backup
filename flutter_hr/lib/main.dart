import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/hr_provider.dart';
import 'screens/admin_dashboard_screen.dart';
import 'screens/employee_dashboard_screen.dart';
import 'screens/department_screen.dart';
import 'screens/employee_screen.dart';
import 'screens/login_screen.dart';
import 'screens/leave_management_screen.dart';
import 'screens/attendance_screen.dart';
import 'screens/advance_salary_screen.dart';
import 'screens/bonus_management_screen.dart';
import 'screens/salary_management_screen.dart';
import 'screens/profile_screen.dart';
import 'services/api_service.dart';
import 'utils/app_theme.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await ApiService.loadSavedUserInfo();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => HrProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'HR Management',
      theme: AppTheme.themeData,
      home: ApiService.userId != null ? const MainNavigationScreen() : const LoginScreen(),
    );
  }
}

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({super.key});

  static _MainNavigationScreenState? of(BuildContext context) => context.findAncestorStateOfType<_MainNavigationScreenState>();

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  String _currentScreen = 'Home';
  bool _isAdmin = false;

  void setScreen(String screenName) {
    setState(() => _currentScreen = screenName);
  }

  @override
  void initState() {
    super.initState();
    _isAdmin = ApiService.userRole?.toUpperCase() == 'ADMIN';
  }

  Widget _getScreen() {
    switch (_currentScreen) {
      case 'Home': return const HomeScreen();
      case 'Dashboard': return _isAdmin ? const AdminDashboardScreen() : const EmployeeDashboardScreen();
      case 'Attendance': return const AttendanceScreen();
      case 'Leave': return const LeaveManagementScreen();
      case 'Advance Salary': return const AdvanceSalaryScreen();
      case 'Bonuses': return const BonusManagementScreen();
      case 'Salaries': return const SalaryManagementScreen();
      case 'Profile': return const ProfileScreen();
      case 'Departments': return const DepartmentScreen();
      case 'Employees': return const EmployeeScreen();
      default: return const HomeScreen();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_currentScreen),
      ),
      drawer: Drawer(
        child: Column(
          children: [
            GestureDetector(
              onTap: () {
                setScreen('Profile');
                Navigator.pop(context);
              },
              child: UserAccountsDrawerHeader(
                accountName: Text(ApiService.userName ?? 'User'),
                accountEmail: Text(ApiService.userEmail ?? ''),
                currentAccountPicture: const CircleAvatar(child: Icon(Icons.person)),
                decoration: const BoxDecoration(color: AppTheme.primary),
              ),
            ),
            Expanded(
              child: ListView(
                children: [
                  _navTile('Home', Icons.home),
                  _navTile('Dashboard', Icons.dashboard),
                  _navTile('Profile', Icons.person),
                  _navTile('Attendance', Icons.timer),
                  _navTile('Leave', Icons.event_note),
                  _navTile('Advance Salary', Icons.money),
                  _navTile('Bonuses', Icons.monetization_on),
                  _navTile('Salaries', Icons.account_balance_wallet),
                  if (_isAdmin) ...[
                    const Divider(),
                    _navTile('Departments', Icons.business),
                    _navTile('Employees', Icons.people),
                  ],
                ],
              ),
            ),
            ListTile(
              leading: const Icon(Icons.logout, color: Colors.red),
              title: const Text('Logout', style: TextStyle(color: Colors.red)),
              onTap: () async {
                await ApiService.logout();
                if (mounted) Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const LoginScreen()));
              },
            ),
          ],
        ),
      ),
      body: _getScreen(),
    );
  }

  Widget _navTile(String title, IconData icon) {
    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      selected: _currentScreen == title,
      onTap: () {
        setScreen(title);
        Navigator.pop(context);
      },
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final navState = MainNavigationScreen.of(context);

    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Welcome, ${ApiService.userName}!',
            style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 10),
          const Text('Choose an option from the menu to get started.'),
          const SizedBox(height: 20),
          Expanded(
            child: GridView.count(
              crossAxisCount: 2,
              childAspectRatio: 1.1,
              mainAxisSpacing: 10,
              crossAxisSpacing: 10,
              children: [
                _homeCard(context, 'Attendance', Icons.timer, Colors.blue, 
                  () => navState?.setScreen('Attendance')),
                _homeCard(context, 'Apply Leave', Icons.event_note, Colors.orange, 
                  () => navState?.setScreen('Leave')),
                _homeCard(context, 'Advance Salary', Icons.payments, Colors.green, 
                  () => navState?.setScreen('Advance Salary')),
                _homeCard(context, 'Bonuses', Icons.monetization_on, Colors.redAccent, 
                  () => navState?.setScreen('Bonuses')),
                _homeCard(context, 'Salaries', Icons.account_balance_wallet, Colors.teal, 
                  () => navState?.setScreen('Salaries')),
                _homeCard(context, 'My Dashboard', Icons.dashboard, Colors.purple, 
                  () => navState?.setScreen('Dashboard')),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _homeCard(BuildContext context, String title, IconData icon, Color color, VoidCallback onTap) {
    return Card(
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 36, color: color),
              const SizedBox(height: 8),
              Flexible(
                child: Text(
                  title, 
                  style: const TextStyle(fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
