describe('Not found page', () => {
  it('should redirect unknown routes to 404', () => {
    cy.visit('/this-route-does-not-exist');

    cy.url().should('include', '/404');
    cy.contains('Page not found !').should('be.visible');
  });
});
