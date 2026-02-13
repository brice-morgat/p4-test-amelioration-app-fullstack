/**
 * @type {Cypress.PluginConfig}
 */
import registerCodeCoverageTasks from '@cypress/code-coverage/task';

export default (on, config) => {
  registerCodeCoverageTasks(on, config);
  return config;
};
