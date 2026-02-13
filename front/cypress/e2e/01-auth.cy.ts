describe('Auth pages', () => {
  it('should register successfully', () => {
    cy.intercept('POST', '/api/auth/register', { statusCode: 200, body: {} }).as('register');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@mail.com');
    cy.get('input[formControlName=password]').type('secret123');
    cy.contains('button', 'Submit').click();

    cy.wait('@register');
    cy.url().should('include', '/login');
  });

  it('should display an error when register fails', () => {
    cy.intercept('POST', '/api/auth/register', { statusCode: 400, body: {} }).as('registerError');

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@mail.com');
    cy.get('input[formControlName=password]').type('secret123');
    cy.contains('button', 'Submit').click();

    cy.wait('@registerError');
    cy.contains('An error occurred').should('be.visible');
  });

  it('should login successfully and redirect to sessions', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'john.doe@mail.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: true,
      },
    }).as('login');

    cy.intercept('GET', '/api/session', { statusCode: 200, body: [] }).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('john.doe@mail.com');
    cy.get('input[formControlName=password]').type('secret123');
    cy.contains('button', 'Submit').click();

    cy.wait('@login');
    cy.wait('@sessions');
    cy.url().should('include', '/sessions');
    cy.contains('Logout').should('be.visible');
  });

  it('should display an error when login fails', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: {} }).as('loginError');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('john.doe@mail.com');
    cy.get('input[formControlName=password]').type('wrong-password');
    cy.contains('button', 'Submit').click();

    cy.wait('@loginError');
    cy.contains('An error occurred').should('be.visible');
  });
});
