//
//  PasskeyAuthScreen.swift
//  PasskeysRailsDemo
//
//  Created by Troy Anderson on 7/31/23.
//

import SwiftUI
import AuthenticationServices

struct PasskeyAuthScreen: View {
    @EnvironmentObject var modelData: ModelData
    @Environment(\.authorizationController) private var authorizationController
    @State var username = ""
    @State var className = ""
    @State var registerLog: [String] = []
    @State var authLog: [String] = []

    var body: some View {
        NavigationStack {
            VStack {
                ScrollView {
                    VStack(spacing: 20) {
                        Divider()
                        
                        VStack {
                            Text("Registration")
                                .font(.headline)
                            TextInput(.username, text: $username)
                                .textContentType(.username)
                                .autocapitalization(.none)
                        
                            registerButton()
                            registerFooter()
                                .font(.subheadline)
                            LogView(entries: $registerLog)
                                .animation(.default, value: registerLog)
                        }
                        
                        Divider()
                        
                        VStack {
                            Text("Authentication")
                                .font(.headline)
                            authenticateButton()
                            authenticateFooter()
                                .font(.subheadline)
                            LogView(entries: $authLog)
                                .animation(.default, value: authLog)
                        }
                    }
                }
                
                Spacer()
                Divider()
                StatusView()
                Divider()
            }
            .padding(.horizontal)
            .buttonStyle(.borderedProminent)
            .navigationTitle("Native Passkeys")
        }
    }
    
    private func registerButton() -> some View {
        Button {
            register(username: username, className: className)

        } label: {
            Text("Register")
                .frame(minWidth: 200)
        }
        .frame(maxWidth: .infinity)
    }
    
    private func authenticateButton() -> some View {
        Button {
            authenticate(username: username)
        } label: {
            Text("Authenticate")
                .frame(minWidth: 200)
        }
        .frame(maxWidth: .infinity)
    }
    
    private func registerFooter() -> some View {
        VStack(spacing: 6) {
            Text("POST \(API.passkeyRegisterStart)")
            Text("POST \(API.passkeyRegisterFinish)")
        }
        .frame(maxWidth: .infinity)
    }
    
    private func authenticateFooter() -> some View {
        VStack(spacing: 6) {
            Text("POST \(API.passkeyLoginStart)")
            Text("POST \(API.passkeyLoginFinish)")
        }
        .frame(maxWidth: .infinity)
    }
    
    private func register(username: String, className: String) {
        Task {
            do {
                // Get the challenge from the server
                let creationOptions = try await modelData.api.passkeyRegistrationChallenge(username: username)
                
                // Request that a passkey be created
                let credential = try await modelData.passkeyManager.createPasskeyAccount(authorizationController: authorizationController, creationOptions: creationOptions, requestOptions: [])
                
                // Request an auth token from PasskeysRails for use in future authenticated API calls
                let auth = try await modelData.api.passkeyRegister(credential: credential)
                
                // Store the token for use in subsequent API calls
             //   modelData.loginWith(auth)
                
              registerLog.append("[Auth OK] username: \(auth.username)")
            } catch {
                registerLog.append(error.localizedDescription)
            }
        }
    }
    
    private func authenticate(username: String) {
        Task {
            do {
                // Get the challenge from the server
                let assertionOptions = try await modelData.api.passkeyAuthenticationChallenge(username: username)
                
                // Request that a passkey be retrieved (OS handles the UI/UX)
                let credential = try await modelData.passkeyManager.signIntoPasskeyAccount(authorizationController: authorizationController, assertionOptions: assertionOptions)
                
                // Request an auth token from PasskeysRails for use in future authenticated API calls
                let auth = try await modelData.api.passkeyAuthenticate(credential: credential)

                // Store the token for use in subsequent API calls
               // modelData.loginWith(auth)

                authLog.append("[Auth OK] username: \(auth.username)")
            } catch {
                authLog.append(error.localizedDescription)
            }
        }
    }
}

struct PasskeyAuthScreen_Previews: PreviewProvider {
    static var previews: some View {
        PasskeyAuthScreen()
            .environmentObject(ModelData())
    }
}
