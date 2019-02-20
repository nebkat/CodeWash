# CodeWash

Welcome to the CodeWash

https://refactoring.guru/smells/primitive-obsession

# Code Smells

- [Bloated Code](#bloated-code)
  - [Long Methods](#long-methods)
  - [Long Names](#long-names)
  - [Long Classes](#long-classes)
  - [Long Parameter List](#long-parameter-list)
  - [Arrowhead Indentation](#arrowhead-indentation)
  - [Switch Statements](#switch-statements)
  - [Primitive Obsession](#primitive-obsession)
- [OOP Violations](#oop-violations)
  - [Temporary Fields](#temporary-fields)
  - [Data Class](#data-class)
  - [Refused Bequest](#refused-bequest)
- [Change Preventers](#change-preventers)
  - [Shotgun Surgery](#shotgun-surgery)
  - [Divergent Change](#divergent-change)
- [Couplers](#couplers)
  - [Feature Envy - Excessive Use of Other Classes](#feature-envy---excessive-use-of-other-classes)
  - [Inappropriate Intimacy](#inappropriate-intimacy)
  - [Message Chains](#message-chains)
  - [Middle Man](#middle-man)

## Bloated Code
It's bad to make classes do too much and it's also not great to make them do too little, there's a little grey area in between and that's where you want your code.

### Long Methods
------

Since it's easier to write code than read it, this *smell* can go unnoticed until a method becomes obscenely long. **Long methods** are the perfect hiding place for duplicate code, so there's no shame in splitting up medium to large methods into smaller ones. Just give them meaningful names and no one will give them a second glance.

### Long Names
------

This one's a bit topical. Some will argue that there's nothing wrong with having **long names** and there's reason for it. If the name is carrying essential information to the variable/method then yes a **long name** is ok, but **long names** can be an indication of a violation of the single responsibility principle, i.e. methods should only perform one specific task.

### Long Classes
------

Classes are a key part to OOP, they should be designed to represent the functionality of a single object. If a class gets too long there is a strong chance that the class is breaking  one of the core ideas of a class, to represent a single object. **Long classes** can often be split up into smaller classes which better represents the objects functionality.

### Long Parameter List

------

While not always a bad code smell, sometimes having a **long list of parameters** for a method call can be a sign that your code can be changed around to more readable and shorter. **Long parameter lists** can be a byproduct of trying to make classes more independent of each other. While this does reduce dependency, it increases the number of parameters needed for the method call. As the number of parameters increases, the harder it becomes to use and read.

If the parameters are result of method calls then the method calls can be moved inside the method call itself. Also if the parameters are the received from another object, this object can be passed to the method and its data retrieved inside the method. Reducing the length of parameter lists makes code more readable, shorter and may possible reveal unnoticed duplicate code elsewhere.

*Note: Don't get rid of parameters from a method just for the sake of doing so, you don't want to cause an unwanted dependency between classes.*

### Arrowhead Indentation
------

Overcomplicated logic in code can often be seen in the shape of an **arrowhead**. Overly complicated logic makes code harder to understand and maintain so keeping logic conditions concise and clear is really important to writing good code.

```java
if (isA()){
    if (isB()){
        if(isC()){
            if(isD()){				// Arrowhead Shape, can probably be simplified
			} else{} 
        } else {}
    } else {}  
} else {}

if (isA() && isB() && isC() && isD())	// Simplified to only line 
```



### Switch Statements
------

While **switch statements** do have their place in programming often they can lead duplicated code scattered throughout the program. The OOP concept of Polymorphism has many advantages, one being when the same set of conditions appear across the program. Updating these conditions can be troublesome if you want to use a different/new type, so using polymorphism reduces dependencies. **Most of the time when you see a switch statement you should consider polymorphism.**

### Primitive Obsession

------

Primitive Obsession can be seen when primitive types are used instead of small objects for simple tasks such as special strings, ranges, etc. This code smell can occur as the result of brief lapses in the programmers thought process when they decide to use a primitive field for storing some data. This then happens multiple times and next thing classes are riddled with primitives instead of objects.

If there is a variety of primitive fields, it may be possible to logically group them into a class. It also may be possible to move the code that operates on these fields into the newly created class as well. Reducing the number of primitives makes code more flexible and makes the code more understandable and organised.

## OOP Violations
We're all about that OOP life.

### Temporary Fields

------

**Temporary Fields** get their values only under certain circumstances. Outside of these circumstances they are empty. **Temporary Fields** are often created for use in algorithms that require a large amount of inputs and rather than using a large number of parameters in the method, the programmer decides to create fields for this data in the class, this fields are only used by the algorithm and not by the rest of the class.

There are several solutions to this code smell. You can extract the temporary fields and all the code operating on them into a separate class. The fields used can also just be parametrized into the method using them. Reducing **Temporary Fields** leads to better code clarity and organisation.

### Data Class
------

A **Data Class** refers to a class that contains only fields and crude methods for accessing them. These are simply containers for data used by other classes. They don't contain any additional functionality and can't independently operate on the data that they own. In OOP the true power of objects is that they can contain behavior types or operations on their data.

One of the best solutions to this code smell is **Encapsulation**. If a class contains public fields then use encapsulation to prevent direct access to the field and require access via a getter and setter only. Also review the client code that uses the **Data Class**. You may find functionality that would be better located in the **Data Class** itself. If this is the case then just move migrate functionality to the data class. After the class has been filled with well thought-out methods you can get rid of some of the old methods for data access that give overly broad access to the class data, e.g. remove setter methods that aren't used etc..

### Refused Bequest

------

**Refused Bequest** can be seen if a subclass only uses some of the methods and properties inherited from its parents. The unneeded methods may simply go unused or be redefined and give off exceptions. **Refused Bequest** often occurs as a result of the motivation of the programmer to create inheritance between classes only by the desire to reuse the code in the superclass, but the superclass and subclass are completely different.

```java
public class Animal {
    private int legs;
    ...
}

// Both dogs and chairs have four legs

public class Dog extends Animal {
    ...
}

public class Chair extends Animal {
    ...
}

// Solution - Split legs into its own class and inherit from it 
public class Legs {
    ...
}

public class Animal extend Legs {
    ...
}

public class Chair extend Legs {
    ...
}
```

**Refused Bequest** can be eliminated by following a general rule of inheritance, if the superclass and subclass have nothing in common then you should consider some other way of class association such as delegation.

Or if inheritance is appropriate then get rid of unneeded fields and methods in the subclass. Move them into a new subclass and set them to inherit from the new class.

## Change Preventers

These smells mean that if you need to change something in one place, you have to make many changes in other places too as a result. This makes code very difficult and time consuming to maintain.

### Shotgun Surgery

------



### Divergent Change

------



## Couplers

These smells result in excessive coupling between classes or show what happens when coupling is replaced by excessive delegation.

### Feature Envy - Excessive Use of Other Classes

------

**Feature Envy** is a code smell which can occur in methods. A method has **Feature Envy** on another class if it uses more features ( i.e. fields and methods) of another class more than its own. **Feature Envy** can  be avoided simply by moving the methods to the preferred class, i.e. the class it is envious of.

### Inappropriate Intimacy

------

One class uses the internal fields and methods of another class. In OOP the less one class knows about another the better.

### Message Chains

------

**Message chains** occur when a client requests another object, which in turn requests from another object and so on. 

> client --> Class A --> Class B --> 

These chains highlight how dependent the client on navigating the class structure. If a change is made to these relationships between classes then the client requires modifying. 

**Message chaining** can be removed by adding a new method to the second object in the chain (Class A) which delegates the call to the next object in the chain(Class B). Now the client doesn't know anything about, or depend on Class B.

By avoiding **message chaining** it makes it easier to maintain the client code as the less the client knows about its relationship to other objects the easier it is to make changes as classes are less dependent on each other.

### Middle Man

------

