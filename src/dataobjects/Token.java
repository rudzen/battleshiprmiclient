/*
 * The MIT License
 *
 * Copyright 2016 Rudy Alex Kohn <s133235@student.dtu.dk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dataobjects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Token holder class.
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
public final class Token implements Serializable {

    private static final long serialVersionUID = 5125315929384373192L;

    private AtomicReference<String> token_real;

    public Token(String token) {
        token_real = new AtomicReference<>();
        token_real.set(token);
    }

    public AtomicReference<String> getToken_real() {
        return token_real;
    }

    public void setToken_real(AtomicReference<String> token_real) {
        this.token_real = token_real;
    }

    
    /* fake getters and setters for token */
    public String getToken() {
        return token_real.get();
    }

    public void setToken(String token) {
        token_real.set(token);
    }
}
