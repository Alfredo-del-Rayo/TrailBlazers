import { Request, Response, NextFunction } from 'express';
import { OAuth2Client } from 'google-auth-library';
import { User } from '../models/user'
import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';

dotenv.config();

const googleClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

//https://stackoverflow.com/questions/58666691/verify-google-id-token-with-node-js
export class authenticate {
    async authId(req: Request, res: Response) {
        let new_user = false;
        const { googleId } = req.body;
        if (!googleId) {
          return res.status(400).json({ status: 'error', error: 'ID is required' });
        }

        try{
          const ticket = await googleClient.verifyIdToken({
            idToken: googleId,
            audience: process.env.GOOGLE_CLIENT_ID,
          });

          const response = ticket.getPayload();

          if (!response) {
            return res.status(400).json({ status: 'error', error: 'Bad Request' });
          }

          let user = await User.findOne({ social_id: response.sub });

          if (!user) {
            const first_name = response.given_name?? "Guest"
            const last_name = response.family_name?? "User"
            const user = new User ({
              email: response.email,
              social_id: response.sub,
              first_name: first_name,
              last_name: last_name,
              pace: 1,
              distance: "Short",
              time: "Short",
              banned: false
            });
            await user.save();
            new_user = true;
          }

          if (!user) {
            return res.status(404).json({ message: 'User not found' });
          }

          const token = jwt.sign({ id: user.social_id }, process.env.JWT_SECRET!, { expiresIn: '12h' });

          res.status(200).json({ status: 'success', token, new_user });
        } catch (error) {
          console.log(error)
          res.status(500).json({ status: 'error', error: 'Internal Server Error' });
        }

    };
}