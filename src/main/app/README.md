# Nivio Frontend

## Table of contents

1. [Installation & Settings](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#installation-settings)
2. [Technology used](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#technology-used)
3. [Environment Variables](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#environment-variables)
4. [Styling](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#styling)
5. [Codeanalysis & Tests](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#Codeanalysis-tests)
6. [CI/CD](https://github.com/dedica-team/nivio/blob/develop/src/main/app/README.md#cicd)

# Installation & Settings

**Recommended IDE:**

- [IntelliJ](https://www.jetbrains.com/idea/) (Backend & Frontend)
- [Visual Studio Code](https://code.visualstudio.com/) (Frontend)

**Required:**

- [nodejs v12.6.2 or higher](https://nodejs.org/en/)
- [yarn](https://classic.yarnpkg.com/en/docs/install/)

**Clone project and install dependencies**

1. Clone Nivio project

```bash
    git clone https://github.com/dedica-team/nivio.git
    git pull
```

2. Start the spring backend in IntelliJ

```bash
    mvn clean package
    java -jar target/nivio.jar
```

If you want to see the demo, you have to set the environmental variable in spring boot config to DEMO=1 and run the configuration.

![Spring Config](doc/spring_config.png 'Spring Config')

3. Start Nivio frontend

```bash
   cd src/main/app
   yarn dev
```

Nivio can be reached at http://localhost:3000

# Technology used

| Name                                                                                  | Purpose                                                                                                                                        |
| ------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| [React](https://reactjs.org/)                                                         | JavaScript library for building user interfaces                                                                                                |
| [Typescript](https://www.typescriptlang.org/)                                         | Optional static typing support                                                                                                                 |
| [ESLint](https://eslint.org/)                                                         | Static code analysis                                                                                                                            |
| [npm](https://www.npmjs.com/)                                                         | Package management                                                                                                                             |
| [Prettier](https://prettier.io/)                                                      | Code formatter                                                                                                                                 |
| [React Testing Library](https://testing-library.com/docs/react-testing-library/intro) | React testing utility                                                                                                                          |
| [Jest](https://jestjs.io/)                                                            | JavaScript testing framework                                                                                                                   |
| [node-sass](https://github.com/sass/node-sass)                                        | CSS preprocessor for Node                                                                                                                      |
| [classNames](https://www.npmjs.com/package/classnames)                                | Conditionally joining class names                                                                                                               |
| [HTML React Parser](https://www.npmjs.com/package/html-react-parser)                  | Convert HTML string into React elements                                                                                                        |
| [dateformat](https://www.npmjs.com/package/dateformat)                                | Library to format a date easily                                                                                                                |
| [React Modal](https://www.npmjs.com/package/react-modal)                              | Accessible modal dialog component for React                                                                                                    |
| [React Router](https://reacttraining.com/react-router/web/guides/quick-start)         | Navigation                                                                                                                                     |
| [React SVG Pan Zoom](https://www.npmjs.com/package/react-svg-pan-zoom)                | Pan and zoom features for SVG images                                                                                                           |
| [React Transition Group](https://github.com/reactjs/react-transition-group)           | Set of components for managing component states (including mounting and unmounting) over time, specifically designed with animation in mind |
| [SVG Path Properties](https://www.npmjs.com/package/react-svg-pan-zoom)               | Javascript alternative to getPointAtLength(t) and getTotalLength() functions                                                                   |
| [axios](https://www.npmjs.com/package/axios)                                          | Promise based HTTP client for the browser and node.js                                                                                          |

# Styling

We use [Material-UI](https://material-ui.com/) for most of our styling.

# Environment Variables

We set our `REACT_APP_BACKEND_URL` in `.env.development` to `http://localhost:8080` because our frontend now runs on a different port. This way we can use hot reloading from React while developing because we dont have to rebuild the maven package every time we change something. If you want to run the frontend app on another port or domain in production, you can create a `.env.production` file with the same content as `.env.development`, but change the URL appropriately.

# Codeanalysis, Formatting & Tests

## Formatting

We format our code with [Prettier](https://prettier.io/).

You can format all source files in `src/` with:

```bash
yarn format
```

## ESLint

Linting is done using the standard ES6 rules.

```bash
yarn lint
```

## Test

Run all jest tests:

```bash
yarn test
```

Run tests with coverage and report in `coverage/lcov-report/index.html`:

```bash
yarn test:coverage
```

The configuration is in `./package.json`.

## Context Testing

To simulate the landscape context which holds all server-side status, use `utils/testing/LandscapeContextValue.ts`

## **Stages**

Code can only be pushed if all stages succeed.

## **Pre-Commit**

### **format**

Runs "yarn format:check", to check if all files are properly formatted, and will stop if any errors occur.

## **Pre-Push**

### **lint**

Runs "yarn lint" and will stop if any errors occur.

### **tests**

Runs all tests and will stop if any errors occur or coverage threshold has not been reached.
