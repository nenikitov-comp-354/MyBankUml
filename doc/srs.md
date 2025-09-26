# Software Requirements Specification for MyBankUML

[toc]

## Introduction

The purpose of this document is to analyze and define high-level needs for features to be added to BankUML application.
BankUML is a Java-based application simulating banking operations.
This application is to be extended into MyBankUML by adding 3 features detailed below.

## Problem statement

| Category | Description |
|-|-|
| **The problem of** | Developing a platform that makes it easy for customers to perform their banking operations.
| **Affects** | Customers who want to consult their information, transfer money, or look up other people's accounts.
| **The impact of which** | That it's difficult to manage banking accounts.
| **A successful solution would be** | A banking platform allowing aforementioned operations.

### Product position

| Category | Description |
|-|-|
| **For** | Customers needing an easily managed banking account.
| **Who** | Are looking for one with previously mentioned features.
| **MyBankUML** | Is a Java-based application
| **That** | Enables customers to check their account information and search for other account information.
| **Unlike** | <!-- TODO -->
| **Our product** | <!-- TODO -->

### Stakeholder and user summary

| Name | Description | Responsibilities | Stakeholder
|-|-|-|-|
| **Customer** | A customer that wants to consult their own account information. | Consult all their information. Transfer between their accounts. | Self-represented.
| **System Administratoir** | An administrator responsible for stability of the bank. | See the state of the bank. | Self-represented.
| **Bank employee** | An employee of a branch. | See information about customers in the branch. Transfer between accounts. | Self-represented.

### User environment

- Customers need a PC (laptop a desktop) with a stable network and a JVM installed

## Display account information

### Introduction

This feature provides an interface to consult account attributes (name, account number, type, etc).

### Questions to stakeholders

- Should the interface be graphical or text based?
    - **We get extra credit for GUI, but it could be text based.**
- Who will use this interface?
    How technical / user-friendly should it be?
    - **Functionality is more important then design**
- Is the interface only for consulting account information or modification should be planned too?
    - **Depositing, transferring**

## Role-based permissions

### Introduction

This feature provides a system handling user-specific permissions on which attributes they could consult.
It should include roles (teller, customer, etc).

### Questions to stakeholders

- Is the log-in system planned?
    - **It is planned, Auth0 is an easy option or plain text is good enough too**
- What roles apart from teller, customer, and admin should exist?
    - **Teller, customer, system admin**
- What information should be shown for each role?
    - **Nothing out of the ordinary**

## Search

### Introduction

This feature provides an ability to filter user accounts by their attributes (ID, place of birth, etc).
It should use a database.

### Questions to stakeholders

- Can a user search by multiple attributes at the same time?
    - **It could be anything, if it's logical enough**
- Can a user specify an exact attribute they want to search for?
(example: should there be a separate text field for each attribute or a single field matching one of attributes).
- Is searching with operators (AND, NOT, OR, date ranges), fuzzy / partial search planned?
- Is sorting planned?
- Should we use an actual database for reading and storing account information?
    - **Yes, no preference**
- What is the expected size of the database?
    - **Enough to show different scenarios**

TODO: user stories, use cases, UML diagrams, activity log, first 3 points of examples.

Product features.
