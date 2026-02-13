function login(admin: boolean) {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      admin,
    },
  }).as('login');

  cy.intercept('GET', '/api/session', { statusCode: 200, body: [] }).as('sessions');

  cy.visit('/login');
  cy.get('input[formControlName=email]').type('john.doe@mail.com');
  cy.get('input[formControlName=password]').type('secret123');
  cy.contains('button', 'Submit').click();

  cy.wait('@login');
  cy.wait('@sessions');
}

describe('Me page', () => {
  it('should display account information', () => {
    login(true);

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'john.doe@mail.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: true,
        password: 'secret123',
        createdAt: '2026-01-01T00:00:00.000Z',
        updatedAt: '2026-01-02T00:00:00.000Z',
      },
    }).as('user');

    cy.contains('Account').click();
    cy.wait('@user');

    cy.contains('User information').should('be.visible');
    cy.contains('john.doe@mail.com').should('be.visible');
    cy.contains('You are admin').should('be.visible');
  });

  it('should delete account for non-admin user', () => {
    login(false);

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'john.doe@mail.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
        password: 'secret123',
        createdAt: '2026-01-01T00:00:00.000Z',
        updatedAt: '2026-01-02T00:00:00.000Z',
      },
    }).as('user');

    cy.intercept('DELETE', '/api/user/1', { statusCode: 200, body: {} }).as('deleteUser');

    cy.contains('Account').click();
    cy.wait('@user');

    cy.get('button[color="warn"]').click();
    cy.wait('@deleteUser');
    cy.url().should('include', '/login');
  });
});
