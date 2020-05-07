# Nivio Frontend

## Table of contents

1. [Installation & Settings](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#installation-settings)
2. [Technology used](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#technology-used)
3. [Styling](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#styling)
4. [Codeanalysis & Tests](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#Codeanalysis-tests)
5. [CI/CD](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#cicd)

# Installation & Settings

**Recommended IDE:**

- [IntelliJ](https://www.jetbrains.com/idea/) (Backend & Frontend)
- [Visual Studio Code](https://code.visualstudio.com/) (Frontend)

**Required:**

- [nodejs v12.6.2](https://nodejs.org/en/)

**Clone project and install dependencies**

1. Clone Nivio project

```bash
    git clone https://github.com/dedica-team/nivio.git
    git pull
```

2. Install node_modules:

```bash
    cd src/main/app
    npm install
```

3. Start the spring backend in IntelliJ

```bash
    mvn package
    java -jar target/nivio.jar
```

If you want to see the demo you have to set environmental variable in spring boot config to DEMO=1 and run the configuration

![Spring Config](doc/spring_config.png 'Spring Config')

4. Start nivio frontend

```bash
   cd src/main/app
   npm run start
```

Nivio can be reached at http://localhost:3000

# Technology used

| Name                                                                                  | Purpose                                                                      |
| ------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| [React](https://reactjs.org/)                                                         | JavaScript Library for building user interfaces                              |
| [Typescript](https://www.typescriptlang.org/)                                         | Optional static typing support                                               |
| [ESLint](https://eslint.org/)                                                         | Static Codeanalysis                                                          |
| [npm](https://www.npmjs.com/)                                                         | Package management                                                           |
| [Prettier](https://prettier.io/)                                                      | Code formatter                                                               |
| [React Testing Library](https://testing-library.com/docs/react-testing-library/intro) | React Testing Utility                                                        |
| [Jest](https://jestjs.io/)                                                            | JavaScript Testing Framework                                                 |
| [node-sass](https://github.com/sass/node-sass)                                        | CSS preprocessor for Node                                                    |
| [classNames](https://www.npmjs.com/package/classnames)                                | Conditionally joining classNames                                             |
| [HTML React Parser](https://www.npmjs.com/package/html-react-parser)                  | Convert HTML String into React elements                                      |
| [raw.macro](https://www.npmjs.com/package/raw.macro)                                  | Load file contents at compile time                                           |
| [React Modal](https://www.npmjs.com/package/react-modal)                              | Accessible modal dialog component for React                                  |
| [React Hexgrid](https://github.com/Hellenic/react-hexgrid)                            | Build interactive hexagon grids with React                                   |
| [React Router](https://reacttraining.com/react-router/web/guides/quick-start)         | Navigation                                                                   |
| [React SVG Pan Zoom](https://www.npmjs.com/package/react-svg-pan-zoom)                | Pan and zoom features for SVG images                                         |
| [SVG Path Properties](https://www.npmjs.com/package/react-svg-pan-zoom)               | Javascript alternative to getPointAtLength(t) and getTotalLength() functions |

# Styling

We use [Material-UI](https://material-ui.com/) for most of our styling combined with our own .scss files for clean, easy and less CSS. [Learn More](https://sass-lang.com/)

# Codeanalysis, Formatting & Tests

## Formatting

We format our code with [Prettier](https://prettier.io/)  
You can format all src files with:

```bash
npm run format
```

## ESLint

```bash
npm run lint
```

Linting with standard es6 rules

## Test

Run all jest tests

```bash
npm run test
```

Run tests with coverage and report in: coverage/lcov-report/index.html

```bash
npm run test:coverage
```

Config in ./package.json

# CI/CD

## (Github implementation TBD)

## **Stages**

Pull Requests can only be accepted if all stages succeed

### **format**

Runs "npm run format:check" to check if all files are properly formatted and will stop if any errors occure

### **lint**

Runs "npm run lint" and will stop if any errors occure

### **tests**

Runs all tests and will stop if any errors occure or coverage threshold has not been reached
