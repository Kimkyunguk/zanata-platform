import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { mapValues } from 'lodash'
import { createStore, applyMiddleware, compose } from 'redux'
import thunk from 'redux-thunk'
import createLogger from 'redux-logger'
import { history } from './history'
import { syncHistory } from 'react-router-redux'
import WebFont from 'webfontloader'
import { apiMiddleware } from 'redux-api-middleware'
import rootReducer from './reducers'
import Root from './containers/Root'
import { isJsonString } from './utils/StringUtils'

import './styles/style.less'

WebFont.load({
  google: {
    families: [
      'Source Sans Pro:200,400,600',
      'Source Code Pro:400,600'
    ]
  },
  timeout: 2000
})

const routerMiddleware = syncHistory(history)

const loggerMiddleware = createLogger({
  predicate: (getState, action) =>
  process.env && (process.env.NODE_ENV === 'development'),
  actionTransformer: (action) => {
    if (typeof action.type !== 'symbol') {
      console.warn('You should use a Symbol for this action type: ' +
        String(action.type))
    }
    return {
      ...action,
      // allow symbol action type to be printed properly in logs
      type: String(action.type)
    }
  }
})

const finalCreateStore = compose(
  applyMiddleware(
    thunk,
    apiMiddleware,
    routerMiddleware,
    loggerMiddleware
  )
)(createStore)

// Call and assign the store with no initial state
const store = ((initialState) => {
  const store = finalCreateStore(rootReducer, initialState)
  if (module.hot) {
    // Enable Webpack hot module replacement for reducers
    module.hot.accept('./reducers', () => {
      const nextRootReducer = require('./reducers')
      store.replaceReducer(nextRootReducer)
    })
  }
  return store
})()

window.config = mapValues(window.config, (value) =>
  isJsonString(value) ? JSON.parse(value) : value)
// baseUrl should be /zanata or ''
window.config.baseUrl = window.config.baseUrl || ''

const frontendRoot = document.getElementById('root')
if (frontendRoot) {
  render(
    <Root store={store} history={history} />,
    document.getElementById('root')
  )
}

