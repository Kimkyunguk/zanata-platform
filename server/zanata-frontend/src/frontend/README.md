## This is a module to build Zanata frontend javascript projects

This module contains: User profile page, Glossary page, and Zanata side menu bar.

## To run/setup in nodeJS

Navigate to `frontend`, run `make install`

### To run in dev mode http://localhost:8000 (a HTTP server to serve index.html with webpack produced bundle.js)

- need http://localhost:8080/zanata to run separately
`make start`

### Production Build

`make build`

### Run styleguide

`make styleguide-build` followed by `make styleguide-server`


## To generate a jar dependency

```mvn install```

It will build and deploy to local maven repository a jar file containing the javascript bundle.
The jar file can be used directly under any servlet 3 compatible container and the bundle is accessible as static resources.
See [Servlet 3 static resources](http://www.webjars.org/documentation#servlet3).

The following Maven properties can be overridden on the command line with ```-Dkey=value```:

```
<node.version>v5.6.0</node.version>
<yarn.version>v0.18.1</yarn.version>
<yarn.install.directory>${download.dir}/zanata-frontend/node-${node.version}-yarn-${yarn.version}</yarn.install.directory>
```

By default it will try to install npm modules from npm registry (default cache TTL is 10 seconds).


## ignore-scripts

This module is setup to disable lifecycle scripts for yarn command by default. See help using `yarn -h`
The configuration is in .yarnrc in this module.

# Zanata Alpha Editor

This is a single-page React+Redux application that uses REST APIs on the Zanata
server for all its data. It is meant to be packaged with zanata-war as a jar
dependency.

## Build jar

To package the editor as a jar, use maven:

```
mvn package
```

## Development Builds

All the build instructions below are for use when developing this editor on its
own. They are not needed to make the final jar package. They just allow for
faster builds and immediate feedback on code changes.

Make sure you test changes against the current server before checking them in.

## Setup and Deployment
1. Make sure [node and yarn](http://nodejs.org/) are installed. Node: v5.6.0, Yarn: v0.18.1
2. Setup dependencies: `make setup`.
3. Build compressed files: `make build`, files will be in /app/dist


### Run with live reload

Build and run a server: `make watch`.

 - Editor is available at [localhost:8080](http://localhost:8080)
   - the editor will be blank at the base URL, include the project-version to
     show content. The format is
     localhost:8000/#/{project-slug}/{version-slug}/translate
 - Assumes a server is already serving the Zanata REST API.


### Run with live reload and local API server

Build and run server and API server: `make watch-fakeserver`.

 - Editor is available at [localhost:8080](http://localhost:8080)
   - URL for a working document from the default API server [Tiny Project 1, hello.txt to French](http://localhost:8080/#/tiny-project/1/translate/hello.txt/fr)
 - REST API server is available at
   [localhost:7878/zanata/rest](http://localhost:7878/zanata/rest)


## Running tests

Run tests with `make test`.


## Code Guidelines

### Javascript

And [these](https://github.com/zanata/javascript) for Javascript.

Always add documentation.

### CSS

For CSS I am aiming to move to [these guidelines](https://github.com/suitcss/suit/blob/master/doc/README.md).

## ignore-scripts

This module is setup to disable lifecycle scripts for yarn command by default. See help using `yarn -h`
The configuration is in .yarnrc in this module.


To start the webpack dev server (with fake-zanata-server):

```
cd zanata-server/zanata-frontend/src/editor
make watch-fakeserver
```

The new redux app is in `zanata-server/zanata-frontend/src/editor/app`


# App code locations

All code is within `zanata-server/zanata-frontend/src/editor/app`

Entry point is `index.js` (where Redux store is instantiated and Root container
render is started).

- `containers` holds the top level components that do the UI layout.
- `components` holds the smaller reusable components.

- `actions` defines all the actions, imported as needed to components
- `api` has all the REST calls, and the functions defined here are usually used
        in async actions (using redux-thunk-middleware)
- reducers are in `reducers` and combined to a single top-level reducer in
  `reducers/index.js`. They are using React immutability helpers to provide
  the `update()` function

There is some stuff in `index.js` and `containers/Root` that sorts out routing
and connecting the redux provider etc. - this should not need any changes for now.

# App structure and Function

This app is built with Redux and React. Top level components use `connect()` to
control how they connect to the store via the Provider component that wraps the
whole component tree. Simpler components just have their props passed down from
their parent component in the usual React style.


The structure is roughly as follows:


    (nest component somewhere under a provider)

                     +----+ see STORE below
                     v
     <Provider store={store}>
       <MyComponent {...ownProps}/>
     </Provider>

    (connect the component to pick up state from provider)

      connect(mapStateToProps, mapDispatchToProps, [mergeProps])(MyComponent)


    (provider will use the map*/merge* functions and inject the result to this.props)

     +-----------------------------------+   (in index.js)
     |                                   |
     | STORE                             |    applyMiddleware(
     |                                   |      middleware1,
     |                   replaces        |      middleware2,
     |         state <--------+          |      ...)(createStore)(reducer)+
     |           +            |          |                                |
     |           |            |          |                                |
     |           |            | new      | <------------------------------+
     |           |            | state    |         a store is born
     |           v            |          |
     | reducer(state, action) +          |
     |                   ^               |   (reducer provides initial state)
     |                   |               |
     |              modified by          |      reducer(undefined)+
     |              middlewares          |                        |
     |                   |               |                        v
     |                   +               |                  initial state
     |        dispatch(action)           +-------+
     |                                   |       |
     +----------------+------------------+       |
                      |                          v
                      |    mapDispatchToProps(dispatch, ownProps)+
                      |                                          |
                      v                                          |
    mapStateToProps(state, ownProps)+     +----------------------+
                                    |     |
                           +--------+     |
                           v              v
       +--> mergeProps(stateProps, dispatchProps, ownProps)+
       |                                                   |
       +                                                   v
      (usually leave as default)                     this.props
                                               (in class MyComponent)


## How stuff gets from the redux store to components

Some components are "connected", meaning they use the `connect()` function - it
works with the `<Provider>` component to link the store to components near the
top of the component tree.

When I define my `Foo` component I connect it like:

`export default connect(mapStateToProps, mapDispatchToProps)(Foo)`

This will merge the objects that those two functions generate into whatever props
are passed to the component from its parent, to give the final props object the
component sees.

Google those functions for more info.


## What is available to reducer functions

The reducer functions are pure functions that take the current state and an
action object (which just has an action type and some other data). Their job
is to return a new state object based on the action (and a default state when
no state existed yet).

Our top-level reducer function is made using `combineReducers()`, which separates
the handling of different slices of state (e.g. state.phrase is handled by the
phrase reducer, whereas state.context is handled by a different reducer).

This has a limitation - if the phrase reducer needs some context information
(e.g. the selected document id), it has no access to it.

To work around this limitation, I added `middlewares/getstate-in-action.js`
which allows reducers to call `action.getState()` to get the full state object
so they can look up any required reference information.

We could use a custom `combineReducers()` function instead, it is just more work.
We could also rearrange our state so that reducers never need to look at distant
state - this is the ideal to aim for, but may not be practical.
