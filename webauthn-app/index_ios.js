const express = require('express');
const bodyParser = require('body-parser');
const SimpleWebAuthnServer = require('@simplewebauthn/server');
const app = express();

app.use(bodyParser.json());

app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.listen(3000, () => {
  console.log('Server is listening on port 3000');
});

let users = {};
let challenges = {};
//const rpId = 'passkeys-codelab.glitch.me';
const rpId = 'passkeyy.web.app'
//const expectedOrigin =  'android:apk-key-hash:TyBHH9maupZHjVknwsim6o7SjRTAtqI5mZ-jTUc9-hE';
const expectedOrigin = 'android:apk-key-hash:DB30Ucm4zz6I0HlB88yDwfxRflqtn65EWzvhDzePA8Q'

app.post('/register/start', (req, res) => {
    let username = req.body.username;
    let challenge = "ho3f32lcmc8";
    challenges[username] = challenge;
    const pubKey = {
        challenge: challenge,
        rp: {id: rpId, name: '"Passkey Sample"'},
        user: {id: username, name: username, displayName: username},
        pubKeyCredParams: [
            {type: 'public-key', alg: -7},
            {type: 'public-key', alg: -257},
        ],
        authenticatorSelection: {
            authenticatorAttachment: 'platform',
            userVerification: 'required',
            residentKey: 'preferred',
            requireResidentKey: false,
        }
    };
    res.json(pubKey);
    
 });
 app.post('/register/finish', async (req, res) => {
    const username = req.body.username;
    // Verify the attestation response
    let verification;
    try {
        const verifyResponseRegister = {
            response: req.body.data,
            expectedChallenge:decode(Buffer.from(challenges[username], 'utf8').toString('base64')),
            expectedOrigin : "https://passkeyy.web.app"
        }
        verification = await SimpleWebAuthnServer.verifyRegistrationResponse(verifyResponseRegister);
    } catch (error) {
        console.error(error);
        return res.status(400).send({error: error.message});
    }
    const {verified, registrationInfo} = verification;
    if (verified) {
        users[username] = getRegistrationInfo(registrationInfo);
        return res.status(200).send({username: username});
        
    }
    res.status(500).send({error: "no validado"});
 });

 app.post('/login/start', (req, res) => {
    let username = req.body.username;
    if (!users[username]) {
        res.status(404).send(false);
    }

    let challenge = "ho3f32lcmc8";
    challenges[username] = challenge;
    res.json({
        challenge: challenges[username],
        rpId: rpId,
        allowCredentials: [{
            type: 'public-key',
            id: users[username].credentialID,
            transports: ['internal'],
        }],
        userVerification: 'required',
    });
 });

 app.post('/login/finish', async (req, res) => {
    let username = req.body.username;
    if (!users[username]) {
        res.status(404).send(false);
    }
    let verification;
    const user = users[username];
    const authenticatorInfo = getSavedAuthenticatorData(user);
    let challenge = "ho3f32lcmc8";
    challenges[username] = challenge;
    try {
    
        const dataToVerificate = {
            expectedChallenge: decode(Buffer.from(challenges[username], 'utf8').toString('base64')),
            response: req.body.data,
            authenticator: authenticatorInfo,
            expectedRPID: rpId,
            expectedOrigin: "https://passkeyy.web.app"
        }
        verification = await SimpleWebAuthnServer.verifyAuthenticationResponse(dataToVerificate);
    } catch (error) {
        console.error(error);
        return res.status(400).send({error: error.message});
    }
 
    const {verified} = verification;
    if (verified) {
        return res.status(200).send({username: username});
    }
    res.status(500).send({error: "no validado"});
 });

 app.post("/challenge", (req, res) => {
req.session.challenge = null;

  let credentialRequest = {
    status: "success",
    challenge: helpers.randomBase64URLBuffer(32),
    rp: {
      name: "passkey.allisonpoppe.dev",
    },
  };

  req.session.challenge = credentialRequest.challenge.toString();

  res.json(credentialRequest);
});


 function getNewChallenge() {
    return Math.random().toString(36).substring(2);
}

function convertChallenge(challenge) {
    return btoa(challenge).replaceAll('=', '');
}

function uintToString(a) {
    const base64string = btoa(String.fromCharCode(...a));
    return base64string.replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}

function base64ToUint8Array(str) {
    str = str.replace(/-/g, '+').replace(/_/g, '/').replace(/\s/g, '');
    return new Uint8Array(Array.prototype.map.call(atob(str), (c) => c.charCodeAt(0)));
}

function getSavedAuthenticatorData(user) {
    return {
        credentialID: user.credentialID,
        credentialPublicKey: user.credentialPublicKey,
        counter: user.counter,
    }
}

function getRegistrationInfo(registrationInfo) {
    const {credentialPublicKey, counter, credentialID} = registrationInfo;
    return {
        credentialID:credentialID,
        credentialPublicKey: credentialPublicKey,
        counter,
    }
}

var decode = function base64ToBase64URL(input) {
  return input
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}
