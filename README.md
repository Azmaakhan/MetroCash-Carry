# Metro POS System (SCD Term Project)

This repository contains the codebase for the **Metro POS System**, developed as part of the Software Construction and Development (SCD) term project. The project is a comprehensive point-of-sale (POS) system with role-based functionality for managing branches, employees, vendors, and sales data.

---

## Features Overview

### 1. First Screen: Splash Window
- **Splash Window:** Displays a loading screen upon application startup.

---

### 2. Second Screen: Login System
Users can log in based on their roles:
- **Super Admin**
- **Admin/Branch Manager**
- **Cashier**
- **Data Entry Operator**

---

### 3. Role-Based Functionalities

#### **Super Admin**
- **Create Branches**
  - Attributes: `Branch ID/Code`, `Name/City/Active Status`, `Address`, `Phone`
- **Add Branch Managers**
  - Attributes: `Name`, `Branch Code`, `Address`, `Phone`, etc.
- **Reports Generation**
  - Time-based reports: `Today`, `Weekly`, `Monthly`, `Yearly`, or Custom Range
  - Metrics: `Sales`, `Remaining Stock`, `Profit`
  - Visualizations: Graphs/Charts for key metrics.

#### **Admin/Branch Manager**
- **First Login**: Must reset passwords for all employees.
- **Add Employees**:
  - **Cashier** and **Data Entry Operator**
  - Default Password: `Password_123`
- Employee Attributes:
  - `Name`, `Employee Number`, `Email`, `Branch Code`, `Salary`, etc.

#### **Data Entry Operator**
- **First Login**: Must change their password.
- **Manage Vendor Information**:
  - Add or fetch vendor details.
- **Enter Product Information**:
  - Attributes: `Name`, `Category`, `Original Price`, `Sale Price`, `Price by Unit`, `Price by Carton`.

- **Offline Functionality**:
  - Save data locally in `.txt` files when there is no internet.
    - `data.txt`: Stores unsaved data.
    - `temp.txt`: Stores a `true/false` flag to indicate whether unsaved data exists.
  - On reconnect, the system will prompt to save data in the database.

#### **Cashier**
- **Generate Sales**:
  - Enter purchased product details.
  - Automatically deduct sold items from the database.

---

## Setup Instructions

1. Clone the repository:

   git clone https://github.com/yourusername/metro-pos-system.git
   cd metro-pos-system
   
2. Install dependencies (SQL & JFreeChart).
3. Run the application:
   
   java -jar MetroPOSSystem.jar

---

## Technical Highlights

- **User Authentication**:
  - Role-based access control with first-login password reset.
- **Database Integration**:
  - Handles offline scenarios with `.txt` files for data backup.
- **Data Visualization**:
  - Graphs and charts for performance reports.
- **Employee Management**:
  - Hierarchical employee addition by roles.

---

## Folder Structure

```
├── src/
│   ├── main/
│   │   ├── splash/           # Splash screen logic
│   │   ├── auth/             # Login and user authentication
│   │   ├── super_admin/      # Super Admin screens and logic
│   │   ├── branch_manager/   # Branch Manager screens and logic
│   │   ├── data_entry/       # Data Entry Operator functionality
│   │   ├── cashier/          # Cashier sales functionality
│   └── utils/                # Helper functions and utilities
├── resources/
│   ├── styles/               # UI styling and themes
│   ├── assets/               # Images and icons
│   └── temp/                 # Offline storage files (data.txt, temp.txt)
├── README.md                 # Project documentation
└── MetroPOSSystem.jar        # Executable JAR file
```

---

## Future Enhancements
- Enhanced UI/UX for smooth user experience.
- Advanced analytics with machine learning.
- Cloud integration for real-time data syncing.

---

## Contributors
- **Azma** (Lead Developer)

---

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
```

Let me know if you want to tweak or add anything! 🚀
