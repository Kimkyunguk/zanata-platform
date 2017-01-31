import { configure } from '@kadira/storybook';

function loadStories() {
  require('../app/components/stories.js')
}

configure(loadStories, module);
