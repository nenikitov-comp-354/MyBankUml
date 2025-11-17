# 🏦 BankUml: Banking System Simulation

Welcome to **BankUml**, a Java-based banking application designed to simulate core banking operations such as account management, transactions, and receipts.

This project demonstrates the use of Object-Oriented Programming (OOP) principles, including **Inheritance**, **Encapsulation**, **Abstraction**, and **Polymorphism**, strictly following the provided UML diagram.

## 📌 Features

- **Account Management**: Create and manage multiple types of bank accounts.
- **Transaction Handling**: Simulate payments and generate receipts.
- **UML-Driven Design**: Class structure directly follows the given UML diagram.

<!--
TODO: Insert a finished diagram here
## 📊 Diagram
-->

## 🚀 How to Run

Make sure you have the following installed:

- Java
- Maven
- Docker with docker compose

1. Clone the repository:
    ```sh
    git clone https://github.com/M-PERSIC/BankUml.git
    cd BankUml
    ```
2. Compile the code:
    ```sh
    mvn package
    ```
3. Reset the database (optional)
    ```sh
    docker compose down -v
    ```
4. Run the program:
    ```sh
    docker compose up
    java -jar target/BankUml-1.0-full.jar
    ```

---

Originally developed by [@shayanaminaei](https://github.com/shayanaminaei)
