Welcome to the CodeWash

# Code Smells

- [Bloated Code](#bloated-code)
  - [Long Methods](#long-methods)
  - [Long Names](#long-names)
  - [Long Classes](#long-classes)
  - [Arrowhead Indentation](#arrowhead-indentation)
  - [Switch Statements](#switch-statements)
- [OOP Violations](#oop-violations)
  - [Temporary Fields](#temporary-fields)
  - [Violations of Data Hiding](#violations-of-data-hiding)
- [Feature Envy - Enxcessive Use of Other Classes](#feature-envy-excessive-use-of-other-classes)

### Bloated Code

It's bad to make classes do too much and it's also not great to make them do too little, there's a little grey area in between and that's where you want your code.

#### Long Methods

Since it's easier to write code than read it, this *smell* can go unnoticed until a method becomes obscenely long. **Long methods** are the perfect hiding place for duplicate code, so there's no shame in splitting up medium to large methods into smaller ones. Just give them meaningful names and no one will give them a second glance.

#### Long  Names

This one's a bit topical. Some will argue that there's nothing wrong with having **long names** and there's reason for it. If the name is carrying essential information to the variable/method then yes a **long name** is ok, but **long names** can be an indication of a violation of the single responsibility principle, i.e. methods should only perform one specific task.

#### Long Classes

Classes are a key part to OOP, they should be designed to represent the functionality of a single object. If a class gets too long there is a strong chance that the class is breaking  one of the core ideas of a class, to represent a single object. **Long classes** can often be split up into smaller classes which better represents the objects functionality.

#### Arrowhead Indentation

Overcomplicated logic in code can often be seen in the shape of an arrowhead. Overly complicated logic makes code harder to understand and maintain so keeping logic conditions concise and clear is really important to writing good code.

#### Switch Statements

While **switch statements** do have their place in programming often they can lead duplicated code scattered throughout the program. The OOP concept of Polymorphism has many advantages, one being when the same set of conditions appear across the program. Updating these conditions can be troublesome if you want to use a different/new type, so using polymorphism reduces dependencies. **Most of the time when you see a switch statement you should consider polymorphism.**

### OOP Violations

#### Temporary Fields

Local variables that are specific to a method are defined at a class level.

#### Violations of Data Hiding

#### Refused Bequest

A class overrides an inherited method in a way that undermines it's functionality.

### Feature Envy - Excessive Use of Methods From Other Classes

**Feature Envy** is a code smell which can occur in methods. A method has **Feature Envy** on another class if it uses more features ( i.e. fields and methods) of another class more than its own. **Feature Envy** can  be avoided simply by moving the methods to the preferred class, i.e. the class it is envious of.
