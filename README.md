# The FCL Language (Flexible Configuration Language)  

## Motivation

The development of **FCL** (Flexible Configuration Language) is driven by the need to simplify and enhance the process of creating and managing complex configurations. In modern development environments, configurations often become intricate, involving various data formats and nested structures. FCL is designed to address these challenges by providing a unified, flexible language that integrates seamlessly with Integrated Development Environments (IDEs). The primary goal is to offer developers an improved editing experience, enabling better syntax highlighting, auto-completion, formatting, and error diagnostics directly within their IDE. By doing so, FCL aims to reduce the complexity and time involved in configuration management, allowing developers to focus more on their core development tasks.

## Overview

FCL streamlines configuration file creation and management, offering direct use of keys as tag names and supporting a variety of data types. This specification includes the ability to embed JSON and YAML strings, identified by their MIME types `text/json` and `text/yaml`, facilitating seamless IDE integration for editing, retrieving, and managing configurations.

## Document Structure

Configurations in FCL are defined within a `<config>` root element. Child elements represent individual configuration entries, leveraging the key as the tag name for direct access and clarity.

### Basic Structure Example

```xml
<config>
    <databaseHost type="string">localhost</databaseHost>
    <databasePort type="number">3306</databasePort>
</config>
```

## Supported Data Types

FCL supports a wide range of data types, including 

- text
  - text/json
  - text/yaml
- number
- object
- array

### Data Type Examples

#### String and Number

```xml
<config>
    <username type="text">JohnDoe</username>
    <age type="number">30</age>
</config>
```

#### Object

```xml
<config>
    <userInfo type="object">
        <name type="text">John Doe</name>
        <age type="number">30</age>
    </userInfo>
</config>
```

#### Array

```xml
<config>
    <usernames type="array">
        <item type="text">User1</item>
        <item type="text">User2</item>
    </usernames>
</config>
```

#### Embedded JSON and YAML

```xml
<config>
    <userProfile type="text/json">
        {
            "name": "Jane Doe",
            "age": 29
        }
    </userProfile>
    <projectConfig type="text/yaml">
        name: Example Project
        version: 1.0
    </projectConfig>
</config>
```

## IDE Integration Features

FCL is designed for seamless integration with IDEs, offering:

- **Syntax Highlighting**: Tailored for a wide array of data types including embedded JSON and YAML.
- **Auto-Completion and IntelliSense**: Offers suggestions based on the scope, enhancing editing efficiency.
- **Formatting**: Applies data type-specific formatting rules for a uniform and readable configuration file.
- **Hover Information and Diagnostics**: Provides detailed tooltips and validates configurations in real-time.

## Security Considerations

FCL implementations must include safeguards against security vulnerabilities and validate configuration data to maintain integrity.

## Extensibility

The design of FCL allows for the addition of support for more MIME types and data formats, ensuring adaptability to future requirements.

## Conclusion

By supporting direct embedding of JSON and YAML strings along with native XML-like configuration entries, FCL presents a robust, flexible configuration language. This approach enhances developer productivity through straightforward configuration management and comprehensive IDE integration, directly aligning with the motivation to improve the editing experience for complex configurations.
