import React from 'react'
import { storiesOf } from '@kadira/storybook'
import EditableText from '.'

/*
 * See .storybook/README.md for info on the component storybook.
 */
storiesOf('EditableText', module)
  .add('default', () => (
    <EditableText
      className='editable textInput'
      maxLength={255}
      placeholder='Add a description…'
      emptyReadOnlyText='No description'>
      Test text
    </EditableText>
  ))
