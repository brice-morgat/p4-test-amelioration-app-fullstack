import { TestBed } from '@angular/core/testing';

import { SessionInformation } from '../models/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService (unit)', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created with logged-out default state', () => {
    expect(service).toBeTruthy();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should update state when logIn is called', () => {
    const session: SessionInformation = {
      token: 'jwt-token',
      type: 'Bearer',
      id: 7,
      username: 'test@mail.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    service.logIn(session);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(session);
  });

  it('should reset state when logOut is called', () => {
    const session: SessionInformation = {
      token: 'jwt-token',
      type: 'Bearer',
      id: 7,
      username: 'test@mail.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    service.logIn(session);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit login state changes through $isLogged', () => {
    const emissions: boolean[] = [];

    const subscription = service.$isLogged().subscribe((value) => {
      emissions.push(value);
    });

    service.logIn({
      token: 'jwt-token',
      type: 'Bearer',
      id: 99,
      username: 'events@mail.com',
      firstName: 'Event',
      lastName: 'Tester',
      admin: true,
    });
    service.logOut();

    expect(emissions).toEqual([false, true, false]);

    subscription.unsubscribe();
  });
});
