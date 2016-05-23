/*
 * Copyright 2016 Rudy Alex Kohn <s133235@student.dtu.dk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Simple chat-client interface (testing some RMI stuff)
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
public interface IChatClient extends Remote {
    
    void newMessage(String name, String message) throws RemoteException;
    
    void getAllUsers(ArrayList<String> users) throws RemoteException;
    
    void clearAll() throws RemoteException;
    
}
